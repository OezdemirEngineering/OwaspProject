pipeline {
  agent any
  options { timestamps(); skipDefaultCheckout(true) }

  environment {
    SEMGREP_JSON  = 'semgrep-report.json'
    SEMGREP_SARIF = 'semgrep.sarif'
    SEMGREP_RULE_FILE = 'inline-rule.yml'
  }

  stages {
    stage('Checkout') {
      steps { deleteDir(); checkout scm }
    }

    stage('SAST (Semgrep) + Reports') {
      steps {
        writeFile file: "${env.SEMGREP_RULE_FILE}", text: '''
rules:
- id: no-runtime-exec
  languages: [java]
  message: Detected Runtime.exec(), possible command injection.
  severity: ERROR
  patterns:
    - pattern: Runtime.getRuntime().exec(...)
'''

        // wichtig: wir merken uns den Exitcode, aber brechen hier NICHT hart ab,
        // damit reviewdog danach noch laufen kann.
        sh '''
          set -euo pipefail
          semgrep --version

          # Gate Scan (Exitcode != 0 bei Findings)
          set +e
          semgrep scan --config "${SEMGREP_RULE_FILE}" --json -o "${SEMGREP_JSON}" --error .
          echo $? > semgrep.rc
          set -e

          # SARIF immer erzeugen (für reviewdog)
          semgrep scan --config "${SEMGREP_RULE_FILE}" --sarif -o "${SEMGREP_SARIF}" . || true
        '''
      }
      post {
        always {
          archiveArtifacts artifacts: "${SEMGREP_JSON},${SEMGREP_SARIF},${SEMGREP_RULE_FILE},semgrep.rc",
                           allowEmptyArchive: true, fingerprint: true
        }
      }
    }

    stage('PR Decoration (reviewdog)') {
      when { expression { return env.CHANGE_ID?.trim() } }
      steps {
        withCredentials([string(credentialsId: 'github-token', variable: 'REVIEWDOG_GITHUB_API_TOKEN')]) {
          sh '''
            set -euo pipefail
            reviewdog -version
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

    stage('Fail build if Semgrep findings') {
      steps {
        sh '''
          set -euo pipefail
          RC="$(cat semgrep.rc 2>/dev/null || echo 0)"
          echo "Semgrep gate exit code: $RC"
          if [ "$RC" -ne 0 ]; then
            exit 1
          fi
        '''
      }
    }
  }
}
