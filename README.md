# Introduction
In this project, I have built a web app in Spring Boot. Spring Boot is a Java framework used to create web apps. This app simply is connecting to a database and performing the CRUD operations to it.
So let's dig deeper into the details.

# Connect the web app to the database
First of all, this project is connecting to a publicly shared database which is called postgres-world-db. This database is representing many countries and provides some details about each country such as the population, the area, the capital, and etc. This database is dockerized and shared on the docker-hub. You can get it from [here](<https://hub.docker.com/r/ghusta/postgres-world-db>).
The way that the web app is connecting to the database is the datasource.url property which is set in the [application.properties](https://github.com/Moataz-Mahmoud/CRUD-operations-with-Spring-Boot-Dockerized-/blob/master/country-web-app/src/main/resources/application.properties) file. This datasource url should point to localhost:portNumber/DB-name. But you will find that it's pointing to database:5432/world-db. The reason of that it refers to **database** instead of **localhost** is related to the docker-compose file which will be covered below.

# Perform the CRUD operations
After connecting to the database, the web app is ready to perform the CRUD operations.
All the operations are developed in [WebController](https://github.com/Moataz-Mahmoud/CRUD-operations-with-Spring-Boot-Dockerized-/blob/master/country-web-app/src/main/java/com/example/demo/controller/WebController.java) file.
At first, we need to map the whole class to /countries. This mapping lead you to call localhost:portNumber/countries/entryPoint to access any end point built below instead of localhost:portNumber/EntryPoint. And there are two advantages of such a trick:
- The first one is it's just a better practice to map the whole class in order not to concatinate the entry point name with the port number directly. 
- The second advantage is that Spring Boot is creating a default implementation to that class mapping. So that if you types localhost:8080/countries you will get the result of all the contents of the database. So you will get all the cointries objects in this case without writing even a single line of code. *Magnificent*!

Then, all the endpoints will be emplemented respectively. getCountryByCode, getAllCountries, createCountry, updateCountry, and finally deleteCountry.
All the end points are self-descriptive and commented with all its details.

The key idea in the web controller is to create a CountryRepository, and then use it to call many of the built in functions in it. Let's get a closer look on it from [here](https://github.com/Moataz-Mahmoud/CRUD-operations-with-Spring-Boot-Dockerized-/blob/master/country-web-app/src/main/java/com/example/demo/Repository/CountryRepository.java).
CountryRepository is a very primative class which all it does is extending a built in class in Spring Boot which is JPARepository. This class has too many ready functions which you can access directly to perform the CRUD operations as you can see in the web controller. You just need to give it the generic with two members. The first is the class which the functions of the repository will deal with it. The second is the id. This id will be used in the get functions. So if you provided it an integer id, so you will pass an integer variable to the function. If you provided a string one, you will get by string variable. And so on. The most important hint here is ***the type of the id must be as same as the primary key of the database table***. So in our example, because of that the type of the primary key is string "countryCode" we will provid the generic a string variable.

The last part to take a look on it is the Country class.
This class as you can see [here](https://github.com/Moataz-Mahmoud/CRUD-operations-with-Spring-Boot-Dockerized-/blob/master/country-web-app/src/main/java/com/example/demo/model/Country.java) is just for mapping porpuse. In this class you will define a variable for each attribute in the database table. Also you will need to define the two mutators (setter and getter) for each of them in order to access them.
The primary key of the table must be annotated with @Id annotation to diffrentiate it from the other attributes. That's the all about the model (Country) class. There is no need to define a custome constructor due to that the only point of code we will instantiate the country code is to save the JSON object retrieved by the entry points. And so the JSON object will set the values for you. 
If you look more carefully to both the defined attribute in the model and the already existing in the database, you will find that some attributes aren't there. Simply because I don't need to retrieve them from the databse. Also you can see that all the missed attributes are sometimes nullable, so if you added them to the model in the same way that I added the others, you will get a run time error saying that it's nullable sometimes. So you will need to add "nullable = true" to the annotation to solve such an exception.

# Dockerize the app
Now let's go to the most exciting part of the project. It's docker.
In the repository you can find two files which are related to docker. One is inside the web app and called "Dockerfile" and the other in the home directory and is called "docker-compose.yml". The name of the both are naming conversions. Which means that if you rename any of them, you won't be able to run the solution using docker-compose up command as we will see shortly.

**Just a quick intro to docker ...**
Docker is a tool which is used for the sake of portability and isolation. So once you dockerize your app, you can share it with others. And now all the others need to run this app is just to install docker and run your solution. No need for dependencies, no need for compatibility issues, versions, OS version, ...etc.
For example, this repo is building a java web app which is connecting to a postgres database. But once you clone this project, you won't need to install neither Java nor postgres to be able to run it. The only tool you need to install is docker itself. Also there is no importance what's the operating system you are using. Just install docker, navigate to the project directory via the terminal, and then type "docker-compose up". After that, set down and sip your tea mug and watch the magic which docker is doing for you until the app is upp and running.

##### Close look to the web app Dockerfile
As you can see, this solution consists of two main components. The web service and the database service. So you can conclude that to run it, we need to launch a docker container for the web app and another one for the database and let them communicate with each other to get the results.
Starting from the webservice, you need to create a docker image for this app to be standalone. And the way to do that is to create a Dockerfile in the home directory of the app. This docker file has a convinient structure. You can take a look on how to write it step by step from [this reference](https://docs.docker.com/engine/reference/builder/). But the good news here is that Spring Boot has a ready extension for doing that for you. It's a plugin built by [Spotify](https://www.spotify.com/) which you can view from [here](https://github.com/spotify/docker-maven-plugin). Once you follow the steps in their github repo, you will get the docker file of your project.

##### docker-compose.yml file
Now we have the docker image of the web service and need to build the docker image of the database. There are two approaches here:
 - As we did for the web service, we will create a Dockerfile for the database. Fortunately, who created it also dockerized it and share it on the docker-hub. You can pull it and then you have the ready image of the database.
 - But the second and most preferrable option, is using just by calling the service name and asks the docker-compose file to build this image and he will do it for you.

But wait, what's that docker-compose file, and why we need it?
Dokcercompse is like that part of docker which makes it that extremely revolutionary. You need docker-compose to let the docker images communicate with each others and arrange that communication process.
The docker compose file is defining all the containers which will be started, everyone as a service. And then thoughout too many options, you can orchestrate their communication. 
You can think about the docker compose syntax as the protocol which the images will use to communicate. ***As the web packets using the HTTP protocol to talk to each others over internet, docker containers use docker compose to talk to each others over something called docker network***. By default, the docker-compose file creates a default netwrok for your services, unless you define other networks and start using them. You can read more about docker networks from this [reference](https://blog.docker.com/2016/12/understanding-docker-networking-drivers-use-cases/).

To build the database image you can either write the syntax of docker image inside the database service's part in the docker-compose file, which is considered as rework. Or the better solution is to refer to the docker image on the docker hub and ask the compose file to instantiate a container of it. Then you need to map the ports of the docker image to your local port. The internal port of the database is 5432. You can map it to any port number you need. But just make sure that it's not used on your machine avoiding conflicts and compose errors.

That was the first section of the compose file, the database service.
The second section is the web service. And this service will build the Dockerfile you generated in the web app directory at first. Secondly, it will again map the ports, but this time it will map them for the web service. By default, a web app will be running on 8080, and you can also map it to any port you want. 

Now the docker-compose file can create and run the two containers. But there are still some additional steps to be done.
First, we don't need the web app to try to hit the database before it's ready for receiving it. So in the web service we add depends_on option and tell docker that the web service depends on the database one. And so docker compose will just instantiate the web app after the database is ready to avoid conflicts and unexpected behaviors.
Second, the web app container now don't know that there is another container for the database. So you need to link them to each other which is done by -link option in the docker-compose file.

The final note here that we have the default docker network containing two containers, one for the web app and the other to the database. So in any of them, if you tried to connect to localhost, you are connecting to two different localhosts. So you need to distinguish between the both when you type localhost in your browser.
To do so, you will tell the web app not to hit localhost:portNumber/Db-name to connect to the database. Instead you tell it to hit database:portnumber/Db-name where database is the name of the database service in the docker copmose file.

Finally thanks for your time reading such a long readme file. And don't hisitate to contact me for any further comments or edits. **Thank you!**
