# xades-signer-cr
Este proyecto realiza el firmado digital de los XMLs de factura electrónica para Costa Rica.

En la carpeta https://github.com/johann04/xades-signer-cr/tree/master/xadessignercr/release está el jar ya compilado:

```
Usage:
java -jar xades-signer-cr sign <keyPath> <keyPassword> <xmlInPath> <xmlOutPath>
java -jar xades-signer-cr send <endPoint> <xmlPath> <username> <password>
java -jar xades-signer-cr query <endPoint> <xmlPath> <username> <password>
```

**sign** firma el xml.

**send** envía el xml a hacienda.

**query** consulta el status de una factura basado en su xml (lee el consecutivo del archivo y consulta ese consecutivo).