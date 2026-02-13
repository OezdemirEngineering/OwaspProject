pipeline {
  agent any
  options {
    timestamps()
    skipDefaultCheckout(true)
  }

  environment {
    SEMGREP_JSON  = 'semgrep-report.json'
    SEMGREP_SARIF = 'semgrep.sarif'

    // Inline rule file name (wird im Workspace erzeugt)
    SEMGREP_RULE_FILE = 'inline-rule.yml'
  }

  stages {
    stage('Checkout') {
      steps {
        deleteDir()
        checkout scm
      }
    }

    stage('SAST (Semgrep) + Gate') {
      steps {
        // Regel im Jenkinsfile definieren (nicht im Repo)
        writeFile file: "${env.SEMGREP_RULE_FILE}", text: '''
rules:
- id: no-runtime-exec
  languages: [java]
  message: Detected Runtime.exec(), possible command injection.
  severity: ERROR
  patterns:
    - pattern: Runtime.getRuntime().exec(...)
'''

        sh '''
          set -euo pipefail
          semgrep --version

          # 1) JSON mit Gate (Exitcode != 0 wenn Finding)
          set +e
          semgrep scan --config "${SEMGREP_RULE_FILE}" --json -o "${SEMGREP_JSON}" .
          SEMGREP_RC=$?
          set -e

          # 2) SARIF für reviewdog (immer generieren)
          #    (ohne --error, damit das Erzeugen nicht am Gate scheitert)
          semgrep scan --config "${SEMGREP_RULE_FILE}" --sarif -o "${SEMGREP_SARIF}" . || true

          # Gate wieder "hochreichen"
          exit $SEMGREP_RC
        '''
      }
      post {
        always {
          archiveArtifacts artifacts: "${SEMGREP_JSON},${SEMGREP_SARIF},${SEMGREP_RULE_FILE}",
                           allowEmptyArchive: true, fingerprint: true
        }
      }
    }

    stage('PR Decoration (reviewdog)') {
      when {
        expression { return env.CHANGE_ID?.trim() }
      }
      steps {
        withCredentials([string(credentialsId: 'github-token', variable: 'REVIEWDOG_GITHUB_API_TOKEN')]) {
          sh '''
            set -euo pipefail
            reviewdog -version

            # SARIF -> GitHub PR Review comments
            reviewdog \
              -f=sarif \
              -name="semgrep" \
              -reporter=github-pr-review \
              -level=error \
              < "${SEMGREP_SARIF}"
          '''
        }
      }
    }
  }
}
