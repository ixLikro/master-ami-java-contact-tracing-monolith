# Master - Architekturen moderner Informationssysteme

The goal of this master module was to implement two versions of the same Software and compare them.
A monolithic implementation and a microservice implementation.

This is the monolithic implementation, you can find the microservice implementation [here](https://github.com/ixLikro/master-ami-java-contact-tracing-services).

## Team members
- [@abail0111](https://github.com/abail0111)
- [@bkoern](https://github.com/bkoern)
- [@ixLikro](https://github.com/ixLikro)
- [@LuKlose](https://github.com/LuKlose)

# Contact tracing

This software traces the movements of users,
create heat maps of movement hotspots and provide an interface for sending notifications of contacts with infected individuals.

The goal of this software was to implement a technical showcase rather than a useful real world application.

![readme gif](https://github.com/ixLikro/master-ami-java-contact-tracing-monolith/blob/master/misc/readme_gif.gif?raw=true)

## Architecture overview
![Architecture overview](https://github.com/ixLikro/master-ami-java-contact-tracing-monolith/blob/master/misc/architecture.png?raw=true)

## Run the monolith **
1. Clone this repo
   ```
   git clone https://github.com/ixLikro/master-ami-java-contact-tracing-monolith.git
    ```
2. cd into cloned repo
   ```
   cd master-ami-java-contact-tracing-monolith
   ```
3. give to execution privilege to the script
      ```
   chmod u+x run.bash
   ```
4. Build the container image
    ```
    podman build -f Containerfile -t ami/team1/monolith
    ```
   This may take a while. During the build process a bootable jar of the application server will be created. 
5. Run the container
    ```
    ./run.bash
    ```
   This will run the container and open firefox with the [landingpage](http://localhost:8080/)

** We are using [Podman](https://podman.io/), but docker should be working too. Just replace all podman occurrences with docker.

## Config
The following environment variables can be used:
   - DATA_GENERATOR_URL (default: http://localhost:8081): <br />
     the url of the data generator


## License overview of included 3rd party libraries

Leaflet<br/>
License: BSD<br/>
https://github.com/Leaflet/Leaflet/blob/master/LICENSE 

Leaflet.heat<br/>
License: BSD 2<br/>
https://github.com/Leaflet/Leaflet.heat/blob/gh-pages/LICENSE

Chart.js<br/>
License: MIT<br/>
https://github.com/chartjs/Chart.js/blob/master/LICENSE.md 

Geodaten<br/>
Geodatenzentrum ©GeoBasis-DE / BKG 2018 (VG250 31.12., Daten verändert)<br/>
https://gdz.bkg.bund.de/ 

Einwohnerzahlen<br/>
Statistisches Bundesamt, Wiesbaden 2019 - Gemeindeverzeichnis<br/>
https://www.destatis.de/DE/Themen/Laender-Regionen/Regionales/Gemeindeverzeichnis/_inhalt.html 
