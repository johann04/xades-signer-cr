java -jar release/xadessignercr.jar sign ../test-data/cert.p12 1234 ../test-data/factura.xml ../test-data/out.xml
java -jar release/xadessignercr.jar send https://api.comprobanteselectronicos.go.cr/recepcion-sandbox/v1 ../test-data/out.xml cpf-02-0586-0860@stag.comprobanteselectronicos.go.cr "%]Y_Tc;]YD}+D2*CIj]*"
sleep 2
java -jar release/xadessignercr.jar query https://api.comprobanteselectronicos.go.cr/recepcion-sandbox/v1 ../test-data/out.xml cpf-02-0586-0860@stag.comprobanteselectronicos.go.cr "%]Y_Tc;]YD}+D2*CIj]*"