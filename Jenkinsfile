pipeline {
  agent any
  options { timestamps() }

  environment {
    SEMGREP_JSON = 'semgrep-report.json'
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

          # scan whole repo, auto ruleset
          # exit code !=0 bei Findings -> wir erzwingen trotzdem Report-Erzeugung
          semgrep scan --config auto --json -o "${SEMGREP_JSON}" . || true

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
