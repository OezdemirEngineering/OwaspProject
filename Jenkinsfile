pipeline {
  agent any
  options { timestamps() }

  environment {
    SEMGREP_JSON = 'semgrep-report.json'
  }

  stages {
    stage('Checkout') {
      options { skipDefaultCheckout(true) }
      steps {
        deleteDir()
        checkout scm
      }
    }

    stage('SAST (Semgrep)') {
      steps {
        writeFile file: 'inline-rule.yml', text: """
        rules:
        - id: no-runtime-exec
          languages: [java]
          message: Detected Runtime.exec(), which can lead to command injection vulnerabilities.
          severity: ERROR
          patterns:
            - pattern: Runtime.getRuntime().exec(...)
        """

        sh '''
          set -euo pipefail
          semgrep scan --config inline-rule.yml --json -o "${SEMGREP_JSON}" --error
        '''
      }
      post {
        always {
          archiveArtifacts artifacts: "${SEMGREP_JSON}", allowEmptyArchive: true, fingerprint: true
        }
      }
    }
  }
}
