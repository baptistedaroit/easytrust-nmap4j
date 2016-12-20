#!groovy

def selectNode() {
        ( env.BRANCH_NAME.contains("master") ) ? 'easytrustdev1' : 'easytrustdev2'
}

// Only keep the 10 most recent builds.
properties([[$class: 'jenkins.model.BuildDiscarderProperty', strategy: [$class: 'LogRotator',
									daysToKeepStr: '10',
                                                                        numToKeepStr: '5']
]])

try
{
	// Build a Preproduction branch in order to prepare a production deployment.
	def selectedNode = selectNode()
	timestampedNode(selectedNode)
	{
		withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'dde333be-439f-4ad9-80c5-c1680095d5ac', passwordVariable: 'REPO_SNAPSHOT_PASSWORD', usernameVariable: 'REPO_SNAPSHOT_USERNAME']])
		{
			// See below for what this method does - we're passing an arbitrary environment
			// variable to it so that JAVA_OPTS and MAVEN_OPTS are set correctly.
			withMavenEnv(["JAVA_OPTS=-Xmx1536m -Xms512m -XX:MaxPermSize=1024m -Djavax.net.ssl.trustStore=/release/java/security/cacerts -Djavax.net.ssl.trustStorePassword=changeit",
                          "MAVEN_OPTS=-Xmx1536m -Xms512m -XX:MaxPermSize=1024m -Djavax.net.ssl.trustStore=/release/java/security/cacerts -Djavax.net.ssl.trustStorePassword=changeit",
				          "REPO_SNAPSHOT_USERNAME=${env.REPO_SNAPSHOT_USERNAME}",
				          "REPO_SNAPSHOT_PASSWORD=${env.REPO_SNAPSHOT_PASSWORD}"])
			{
				// Checkout
				stage 'Checking-out easytrust-nmap4j'
				echo "Selected Node ${selectedNode}"
				checkout scm

				// Test nmap
				stage 'Testing nmap4j'
				echo "Testing nmap4j on ${selectedNode}"
				nmap -sV -O -oX easytrustdev2.xml -p 0-60000 149.202.69.119

				// Build Easytrust Server project
//				stage "Building easytrust-nmap4j and deploying to Artifactory Snapshot repo"
//				echo "PATH= ${PWD}/.repository"
//				sh 'rm -Rf ${PWD}/.repository'
//
//				stage "Build & deploy to artifactory"
//				if (selectedNode == "easytrustdev1")
//				{
//					sh 'mvn -V -B -s settings.xml clean deploy -Ppreprod -Dmaven.repo.local=${PWD}/.repository'
//				}
//				else
//				{
//					sh 'mvn -V -B -s settings.xml clean deploy -Pinteg -Dmaven.repo.local=${PWD}/.repository'
//				}
			}
		}
	}
}

catch (err)
{
	stage 'Send Error Notification'
	mail (  to: 'r&d@easytrust.com',
            subject: "Pipeline '${env.JOB_NAME}' - Build #${env.BUILD_NUMBER} - FAILED!",
            body: "<b>Pipeline '${env.JOB_NAME}' - #${env.BUILD_NUMBER} - FAILED!</b><p>To view the results, please check console output at ${env.BUILD_URL}console</p><p>Have a nice day!:-(</p>",
            mimeType:'text/html');
        currentBuild.result = 'FAILURE'
}

// This method sets up the Maven and JDK tools, puts them in the environment along
// with whatever other arbitrary environment variables we passed in, and runs the
// body we passed in within that environment.
void withMavenEnv(List envVars = [], def body) {
        // The names here are currently hardcoded for my test environment. This needs to be made more flexible.
        // Using the "tool" Workflow call automatically installs those tools on the node.
        String mvntool = tool name: "MAVEN 3.3", type: 'hudson.tasks.Maven$MavenInstallation'
        String jdktool = tool name: "JDK1.8", type: 'hudson.model.JDK'

        // Set JAVA_HOME, MAVEN_HOME and special PATH variables for the tools we're using.
        List mvnEnv = ["PATH+MVN=${mvntool}/bin", "PATH+JDK=${jdktool}/bin", "JAVA_HOME=${jdktool}", "MAVEN_HOME=${mvntool}"]

        // Add any additional environment variables.
        for (String item : envVars) 
		{
            mvnEnv.add(item)
        }

        // Invoke the body closure we're passed within the environment we've created.
        withEnv(mvnEnv) 
		{
            body.call()
        }
}

// Runs the given body within a Timestamper wrapper on the given label.
def timestampedNode(String label, Closure body) {
	node(label) 
	{
		wrap([$class: 'TimestamperBuildWrapper']) 
		{
			body.call()
		}
	}
}

