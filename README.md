# large-scale-analysis

### Library
```xml
<dependency>
    <groupId>codes.mydna</groupId>
    <artifactId>large-scale-analysis-lib</artifactId>
    <version>${large-scale-analysis.version}</version>
</dependency>
```

### Docker

*Note: This service requires DB.*

Pull docker image:
```bash
docker pull mydnacodes/large-scale-analysis
```

Run docker image:
```bash
docker run -d -p <PORT>:8080 
    -e KUMULUZEE_DATASOURCES0_CONNECTIONURL=jdbc:postgresql://<DB_HOST>:<DB_PORT>/large-scale-analysis
    -e KUMULUZEE_DATASOURCES0_USERNAME=<DB_USERNAME> 
    -e KUMULUZEE_DATASOURCES0_PASSWORD=<DB_PASSWORD> 
    --name large-scale-analysis-service
    mydnacodes/large-scale-analysis
```