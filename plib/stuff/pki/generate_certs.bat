@echo off
mkdir client
mkdir server
mkdir client\ca

echo RANDFILE=%USERPROFILE%\.rnd > client\ca\cert.info
echo [req] >> client\ca\cert.info
echo default_bits=2048 >> client\ca\cert.info
echo default_keyfile=keyfile.pem >> client\ca\cert.info
echo distinguished_name=req_distinguished_name >> client\ca\cert.info
echo attributes=req_attributes >> client\ca\cert.info
echo prompt=no >> client\ca\cert.info
echo output_password=change >> client\ca\cert.info
echo [req_distinguished_name] >> client\ca\cert.info
echo C=DE >> client\ca\cert.info
echo ST=Berlin >> client\ca\cert.info
echo L=Berlin >> client\ca\cert.info
echo O=DAI-Laboratory, TU-Berlin >> client\ca\cert.info
echo OU=CC-SEC >> client\ca\cert.info
echo CN=AppPETs-CA >> client\ca\cert.info
echo [req_attributes] >> client\ca\cert.info

echo [v3_ca] > client\ca\cert.cfg
echo subjectKeyIdentifier=hash >> client\ca\cert.cfg
echo authorityKeyIdentifier=keyid:always,issuer >> client\ca\cert.cfg
echo basicConstraints=CA:true,pathlen:1 >> client\ca\cert.cfg

openssl genrsa -des3 -passout pass:12345 -out client\ca\ca-apppets-cert.key 2048
openssl req -new -key client\ca\ca-apppets-cert.key -passin pass:12345 -out client\ca\ca-apppets-cert.csr -config client\ca\cert.info
openssl x509 -req -days 3650 -in client\ca\ca-apppets-cert.csr -signkey client\ca\ca-apppets-cert.key -passin pass:12345 -extfile client\ca\cert.cfg -extensions v3_ca -out client\ca\ca-apppets-cert.crt

REM server ca
mkdir server\ca

echo RANDFILE=%USERPROFILE%\.rnd > server\ca\s-cert.info
echo [req] >> server\ca\s-cert.info
echo default_bits=2048 >> server\ca\s-cert.info
echo default_keyfile=keyfile.pem >> server\ca\s-cert.info
echo distinguished_name=req_distinguished_name >> server\ca\s-cert.info
echo attributes=req_attributes >> server\ca\s-cert.info
echo prompt=no >> server\ca\s-cert.info
echo output_password=change >> server\ca\s-cert.info
echo [req_distinguished_name] >> server\ca\s-cert.info
echo C=DE >> server\ca\s-cert.info
echo ST=Berlin >> server\ca\s-cert.info
echo L=Berlin >> server\ca\s-cert.info
echo O=DAI-Laboratory, TU-Berlin >> server\ca\s-cert.info
echo OU=CC-SEC >> server\ca\s-cert.info
echo CN=S-AppPETs-CA >> server\ca\s-cert.info
echo [req_attributes] >> server\ca\s-cert.info

echo [v3_ca] > server\ca\s-cert.cfg
echo subjectKeyIdentifier=hash >> server\ca\s-cert.cfg
echo authorityKeyIdentifier=keyid:always,issuer >> server\ca\s-cert.cfg
echo basicConstraints=CA:true,pathlen:1 >> server\ca\s-cert.cfg

openssl genrsa -des3 -passout pass:s12345 -out server\ca\s-ca-apppets-cert.key 2048
openssl req -new -key server\ca\s-ca-apppets-cert.key -passin pass:s12345 -out server\ca\s-ca-apppets-cert.csr -config server\ca\s-cert.info
openssl x509 -req -days 3650 -in server\ca\s-ca-apppets-cert.csr -signkey server\ca\s-ca-apppets-cert.key -passin pass:s12345 -extfile server\ca\s-cert.cfg -extensions v3_ca -out server\ca\s-ca-apppets-cert.crt

REM client master
mkdir client\master

echo RANDFILE=%USERPROFILE%\.rnd > client\master\master.info
echo [req] >> client\master\master.info
echo default_bits=2048 >> client\master\master.info
echo default_keyfile=keyfile.pem >> client\master\master.info
echo distinguished_name=req_distinguished_name >> client\master\master.info
echo attributes=req_attributes >> client\master\master.info
echo prompt=no >> client\master\master.info
echo output_password=change >> client\master\master.info
echo [req_distinguished_name] >> client\master\master.info
echo C=DE >> client\master\master.info
echo ST=Berlin >> client\master\master.info
echo L=Berlin >> client\master\master.info
echo O=DAI-Laboratory, TU-Berlin >> client\master\master.info
echo OU=CC-SEC >> client\master\master.info
echo CN=MASTER-AppPETs >> client\master\master.info
echo [req_attributes] >> client\master\master.info

echo [v3_ca] > client\master\master.cfg
echo subjectKeyIdentifier=hash >> client\master\master.cfg
echo authorityKeyIdentifier=keyid:always,issuer >> client\master\master.cfg
echo basicConstraints=CA:true,pathlen:0 >> client\master\master.cfg

openssl genrsa -des3 -passout pass:test123 -out client\master\master-apppets-cert.key 2048
openssl req -new -key client\master\master-apppets-cert.key -passin pass:test123 -out client\master\master-apppets-cert.csr -config client\master\master.info
openssl x509 -req -days 3650 -in client\master\master-apppets-cert.csr -CA client\ca\ca-apppets-cert.crt -CAkey client\ca\ca-apppets-cert.key -passin pass:12345 -set_serial 01 -extfile client\master\master.cfg -extensions v3_ca -out client\master\master-apppets-cert.crt
openssl pkcs8 -topk8 -inform PEM -outform DER -in client\master\master-apppets-cert.key -passin pass:test123 -nocrypt > client\master\master-apppets-cert-pkcs.key




REM server master
mkdir server\master

echo RANDFILE=%USERPROFILE%\.rnd > server\master\s-master.info
echo [req] >> server\master\s-master.info
echo default_bits=2048 >> server\master\s-master.info
echo default_keyfile=keyfile.pem >> server\master\s-master.info
echo distinguished_name=req_distinguished_name >> server\master\s-master.info
echo attributes=req_attributes >> server\master\s-master.info
echo prompt=no >> server\master\s-master.info
echo output_password=change >> server\master\s-master.info
echo [req_distinguished_name] >> server\master\s-master.info
echo C=DE >> server\master\s-master.info
echo ST=Berlin >> server\master\s-master.info
echo L=Berlin >> server\master\s-master.info
echo O=DAI-Laboratory, TU-Berlin >> server\master\s-master.info
echo OU=CC-SEC >> server\master\s-master.info
echo CN=S-MASTER >> server\master\s-master.info
echo [req_attributes] >> server\master\s-master.info

echo [v3_ca] > server\master\s-master.cfg
echo subjectKeyIdentifier=hash >> server\master\s-master.cfg
echo authorityKeyIdentifier=keyid:always,issuer >> server\master\s-master.cfg
echo basicConstraints=CA:true,pathlen:0 >> server\master\s-master.cfg

openssl genrsa -des3 -passout pass:stest123 -out server\master\s-master-apppets-cert.key 2048
openssl req -new -key server\master\s-master-apppets-cert.key -passin pass:stest123 -out server\master\s-master-apppets-cert.csr -config server\master\s-master.info
openssl x509 -req -days 3650 -in server\master\s-master-apppets-cert.csr -CA server\ca\s-ca-apppets-cert.crt -CAkey server\ca\s-ca-apppets-cert.key -passin pass:s12345 -set_serial 01 -extfile server\master\s-master.cfg -extensions v3_ca -out server\master\s-master-apppets-cert.crt
openssl pkcs8 -topk8 -inform PEM -outform DER -in server\master\s-master-apppets-cert.key -passin pass:stest123 -nocrypt > server\master\s-master-apppets-cert-pkcs.key

copy client\ca\ca-apppets-cert.crt ..\..\src\main\res\raw\ca_apppets_cert.crt

REM Generating keystores

REM client
del client\trustsore.jks
keytool -import -alias apppets -file client\ca\ca-apppets-cert.crt -keystore client\truststore.jks -storepass changeit

REM server
del server\trustsore.jks
keytool -import -alias s-ca-apppets -file server\ca\s-ca-apppets-cert.crt -keystore server\truststore.jks -storepass changeit

REM print truststores
cls
keytool -v -list -keystore client\truststore.jks -storepass changeit
keytool -v -list -keystore server\truststore.jks -storepass changeit

pause