java -jar release/xadessignercr.jar sign ../test-data/cert.p12 5678 ../test-data/factura.xml ../test-data/out.xml
#echo ---------------------------------------------------------------------------------------------------------
java -jar release/xadessignercr.jar send https://api.comprobanteselectronicos.go.cr/recepcion-sandbox/v1 ../test-data/out.xml cpf-02-0586-0860@stag.comprobanteselectronicos.go.cr '!-UsO|D(|i!o;3s>+/aw'
#echo ---------------------------------------------------------------------------------------------------------
sleep 2
java -jar release/xadessignercr.jar query https://api.comprobanteselectronicos.go.cr/recepcion-sandbox/v1 ../test-data/out.xml cpf-02-0586-0860@stag.comprobanteselectronicos.go.cr '!-UsO|D(|i!o;3s>+/aw'
#echo ---------------------------------------------------------------------------------------------------------
