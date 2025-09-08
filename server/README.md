# Area Checker FastCGI Server (Java)

Java FastCGI server implementation of the area checking laboratory work.

## Overview

This is a complete Java port of the PHP server, implementing the same functionality:
- FastCGI server for handling HTTP requests
- Area checking logic (rectangle, triangle, quarter circle)
- Input validation for coordinates and radius
- Session management for storing calculation results
- HTML response generation

## Architecture

```
src/main/java/com/lab1/
├── FastCGIServer.java          # Main FastCGI server
├── AreaChecker.java            # Area checking logic
├── CoordinatesValidator.java   # Input validation
├── SessionManager.java         # Session management
└── ResponseBuilder.java        # HTML response generation

src/test/java/com/lab1/
├── AreaCheckerTest.java        # Area checking tests
└── CoordinatesValidatorTest.java # Validation tests
```

## Building and Running

### Prerequisites
- Java 11 or higher
- Maven 3.6 or higher

### Build
```bash
mvn clean package
```

### Run
```bash
java -jar target/area-checker-server-1.0.0.jar
```

The server will start on port 9000 and listen for FastCGI requests.

### Run Tests
```bash
mvn test
```

## FastCGI Configuration

### Apache Configuration
Add to your Apache configuration:

```apache
LoadModule fcgid_module modules/mod_fcgid.so

<Directory "/path/to/your/app">
    SetHandler fcgid-script
    Options +ExecCGI
    FcgidWrapper /path/to/java/area-checker-server-1.0.0.jar
</Directory>
```

### Nginx Configuration
```nginx
location /script {
    fastcgi_pass 127.0.0.1:9000;
    fastcgi_param SCRIPT_FILENAME $document_root$fastcgi_script_name;
    include fastcgi_params;
}
```

## API

### Endpoint: `/script`

**Method:** GET

**Parameters:**
- `xVal` (double): X coordinate (-3, -2, -1, 0, 1, 2, 3, 4, 5)
- `yVal` (double): Y coordinate (-5 < y < 5)
- `rVal` (double): Radius (1, 1.5, 2, 2.5, 3)

**Response:** HTML table rows with calculation results

**Example:**
```
GET /script?xVal=1&yVal=1&rVal=2
```

## Area Definition

The area consists of three regions:

1. **First Quadrant (Rectangle):** 0 ≤ x ≤ R/2, 0 ≤ y ≤ R
2. **Fourth Quadrant (Triangle):** Vertices at (0,0), (R,0), (0,-R/2)
3. **Third Quadrant (Quarter Circle):** Center at (0,0), radius R
4. **Second Quadrant:** Always outside the area

## Session Management

Results are stored in memory using a simple session manager. Each calculation result includes:
- Input coordinates (x, y, r)
- Hit/miss result
- Current timestamp
- Execution time

## Differences from PHP Version

1. **Session Storage:** Uses in-memory HashMap instead of PHP sessions
2. **Error Handling:** More robust exception handling
3. **Type Safety:** Strong typing with Java generics
4. **Testing:** Comprehensive unit tests with JUnit 5
5. **Logging:** Structured logging with SLF4J

## Development

### Adding New Features
1. Add new classes in `src/main/java/com/lab1/`
2. Write tests in `src/test/java/com/lab1/`
3. Update this README

### Code Style
- Follow Java naming conventions
- Use meaningful variable names
- Add Javadoc comments for public methods
- Keep methods small and focused

## Troubleshooting

### Common Issues

1. **Port Already in Use**
   - Change port in `FastCGIServer.java`
   - Update web server configuration

2. **FastCGI Connection Failed**
   - Check web server FastCGI configuration
   - Verify server is running on correct port

3. **Validation Errors**
   - Check parameter names (xVal, yVal, rVal)
   - Verify parameter ranges

### Logging
The server uses SLF4J for logging. Logs include:
- Server startup/shutdown
- Request processing
- Error conditions
- Performance metrics

## License

MIT License - same as the original PHP project.
