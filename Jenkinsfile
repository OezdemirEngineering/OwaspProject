pipeline {
  agent any
  options { timestamps() }

  environment {
    SEMGREP_JSON    = 'semgrep-report.json'
    SEMGREP_RULESET = 'p/security-audit'   // z.B. p/owasp-top-ten oder .semgrep/rules.yml
  }

  stages {
    stage('Checkout') {
      options { skipDefaultCheckout(true) } // verhindert den Auto-Checkout davor
      steps {
        deleteDir()
        checkout scm
      }
    }

    stage('SAST (Semgrep)') {
      steps {
        sh '''
          set -euo pipefail
          semgrep --version
          echo "Using Semgrep ruleset: ${SEMGREP_RULESET}"
          semgrep scan --config "${SEMGREP_RULESET}" --json -o "${SEMGREP_JSON}" . || true
          ls -lah "${SEMGREP_JSON}" || true
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
