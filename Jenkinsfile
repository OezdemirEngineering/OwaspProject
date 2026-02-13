pipeline {
  agent any
  options { timestamps() }

  environment {
    SEMGREP_JSON = 'semgrep-report.json'

    // Semgrep Ruleset:
    // Beispiele:
    //   - 'p/security-audit'
    //   - 'p/owasp-top-ten'
    //   - '.semgrep/rules.yml'
    //   - '.semgrep/'  (Ordner)
    SEMGREP_RULESET = 'p/security-audit'
  }

  stages {
    stage('Checkout') {
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

          # scan whole repo using chosen ruleset
          # exit code !=0 bei Findings -> wir erzwingen trotzdem Report-Erzeugung
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
