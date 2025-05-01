build-final:
	javac -d bin src/*.java
	jar cfe Linearizz.jar CCC102.FinalProject.Test -C bin .
