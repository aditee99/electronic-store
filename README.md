# Electronic Store
Creating a backend for an electric store application involves several key components and functionalities to ensure smooth operations for managing products, handling orders, and maintaining customer relationships

## Features

- **User Management**: CRUD operations for users.
- **Product Management**: CRUD operations for products.
- **Category Management**: CRUD operations for product categories.
- **Order Management**: Create, get, and delete orders.
- **Cart Management**: Add, get, and remove items from the shopping cart.

## Technologies Used
* Java
* Spring Boot
* MySQL
* AWS
* Docker
* Maven

## Using the Application
### Installation

1. **Clone the Repository**
  https://github.com/aditee99/electronic-store.git
2. **Set up the MySQL database:**
    - Create a database named `electronic_store`.
    - Update the `application.properties` file with your MySQL database credentials.
3.  **Build the project:**
    ```bash
    mvn clean install
    ```
4. **Run the application:**
    ```bash
    mvn spring-boot:run
    ```
### Deployment to AWS

1. **Set up an AWS EC2 instance:**
    - Log in to the AWS Management Console.
    - Launch an EC2 instance with an appropriate instance type.
    - SSH into the EC2 instance.
2. **Log in to Docker Hub from the EC2 instance:**
    ```bash
    docker login
    ```
3. **Build Docker**
 ```bash
   Build Docker Image : docker build -t electronic .
   docker tag electronic aditeeadhikari98408/electronic1.0
   docker push aditeeadhikari98408/electronic1.0
   docker pull aditeeadhikari98408/electronic1.0

      
4. **Pull the Docker image from Docker Hub:**
    ```bash
    docker pull your-dockerhub-username/electronic-store-backend
    ```

5. **Run the Docker container:**
    ```bash
    docker run -d -p 8080:8080 --name electronic-store-backend your-dockerhub-username/electronic-store-backend
    ```

6. **Set up the database connection on AWS:**
    - Ensure your MySQL database is accessible from the EC2 instance.
    - Update the `application.properties` file within your Docker image or use environment variables to point to your AWS RDS or on-premise MySQL database.
