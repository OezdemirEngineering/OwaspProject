pipeline {
  agent any
  options { timestamps() }

  environment {
    SEMGREP_JSON    = 'semgrep-report.json'
    SEMGREP_RULESET = 'p/java'
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
        sh '''
          set -euo pipefail
          semgrep scan --config "${SEMGREP_RULESET}" --json -o "${SEMGREP_JSON}" .
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
