# js-ssh-console
SSH console to run a JavaScript shell on any Java application

This is this the full source code of a listing for an article on JavaSPEKTRUM magazine. It provides the basic building blocks for adding a SSH server to a Java application providing a shell with access to the apps runtime objects. The shell runs JavaScript, provided by the "Nashorn" runtime implementation.

The folder "js-ssh-console" in this repository can be used as Eclipse project.

Prerequisites:

- This need Java 8 to work
- Libs Apache SSHD and JLine2, provided in folder "js-ssh-console/lib", in classpath
