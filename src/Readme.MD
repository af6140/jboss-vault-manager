# Jboss vault manager

This is a cli utility to manage jboss vault. It can create and manage jboss vault secrets.

java -jar -jbossvaultmgr.jar -help

It has several subcommands:

* set
* get
* remove
* get-config

## Example command

### Command get-config
```bash
java   -jar target/create-vault-1.0-SNAPSHOT.jar vault get-config  -k ./target/test-vault.keystore -p MASK-0a1jbxi4y78avAWpDn/4rI  -s 12345678 -d ./target/test-vault
```


## Simplify command options with input file

```bash
# this will read file ./cmd_args and insert arguments in place
java   -jar target/create-vault-1.0-SNAPSHOT.jar vault get-config  @./cmd_args
```

## Example jboss configuration

```xml
<vault>
  <vault-option name="KEYSTORE_URL" value="./vault/vault.store"/>
  <vault-option name="KEYSTORE_PASSWORD" value="MASK-0cv6awHhb2SC.olzNEHJWH"/>
  <vault-option name="KEYSTORE_ALIAS" value="busa"/>
  <vault-option name="SALT" value="17932486"/>
  <vault-option name="ITERATION_COUNT" value="33"/>
  <vault-option name="ENC_FILE_DIR" value="./vault"/>
</vault>
```