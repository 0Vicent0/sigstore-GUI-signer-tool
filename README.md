# Sigstore GUI signer tool
This is a Java Swing frontend client to [sigstore](https://docs.sigstore.dev/) infraestructure.


It should work on any x86_64 bit computer with Java runtime enviroment. It has been tested with Ubuntu 20.04 LTS with OpenJDK 21.

## INSTALLATION

### Build from source
To build from source use maven and the pom.xml provided in Netbeans or any other Java IDE.

### Binary distribution
A jar file is provided into the releases section so to run the program on Linux simply download the jar file and run it on the terminal:

```
shell> sudo chmod 755 <downloaded.jar> # Making it executable
shell> java -jar <downloaded.jar> # Execute the file
```
In Windows or Mac OX you should be able to double-click the downloaded jar file and run the program.


### Usage
The usage of the program should be almost trivial if you have at least read some of the sigstore official documentation.

1. The first button "Hash an artifact" will execute a 256 bit hash on the selected file and paste it to the screen
2. "Sign an artifact" will use sign the selected file and produce a 'bundle' that matches  this signed file. To check the validity
   of the file you will need to have this bundle.
3. "Verify and artifact" will verify the selected file with his correspondant bundle.
4. "Check hash in reckor ledger" just checks the hash of the file with the hash recorded in rekor public ledger without the need of a bundle
   or if you lost this bundle. This is convenient if you do not want to manage bundles and want to work with hashes.
   Of course you should have signed the artifact before.

![image](gui-interface.png)
