pipeline {
  agent any
  options { timestamps() }

  environment {
    SEMGREP_JSON    = 'semgrep-report.json'
    SEMGREP_RULESET = 'p/java'  // Das offizielle Java-Ruleset von Semgrep
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
          semgrep --version
          echo "Using Semgrep ruleset: ${SEMGREP_RULESET}"
          semgrep scan --config "${SEMGREP_RULESET}" --json -o "${SEMGREP_JSON}" . || true
        '''
      }
      post {
        always {
          archiveArtifacts artifacts: "${SEMGREP_JSON}", allowEmptyArchive: true, fingerprint: true
        }
      }
    }

    stage('Fail on Findings') {
      steps {
        script {
          def findings = readJSON file: "${env.SEMGREP_JSON}"
          def criticalFindings = findings.results.findAll { it.extra.severity == 'ERROR' }
          if (criticalFindings.size() > 0) {
            error("Pipeline failed: ${criticalFindings.size()} critical Semgrep finding(s).")
          } else {
            echo "No critical findings. Proceeding..."
          }
        }
      }
    }
  }
}
