# Fun
OOP class final project utilizing Azure Functions - abbreviated to Fun.

## To deploy:
There are several methods. I'm deploying the functions to Azure by linking my Azure app with my GitHub repository.

## To use:
Just navigate your browser to the url of the deployed application:

https://fun-kd.azurewebsites.net/api/getForm

and enjoy!

## Version 1 features:
1. The application works! It took me 5 hours to set up the test application deployment from GitHub to Azure, but when I figured it out, it felt awesome!
2. The HttpExample function that comes standard when the Functions application is first created is all there is for now.

## Version 2 features:
1. Three or four Azure Functions implemented in Java
2. A function that accepts user input and stores it on the server
3. A function that displays the older entries to the users
4. A secured function displaying top secret information to authorized users only
5. A Java client capable of invoking each of the functions
6. A short tutorial demonstrating how to implement Azure Functions in Java

Now that I have the deployment working, I believe I should be able to deliver all of the above functionality.
Unfortunately, given my deployment experience, the short tutorial may not be so short...

## Related projects:
Fun client:
https://github.com/chris-dz/fun-client.git

Top Secret Fun:
https://github.com/chris-dz/top-secret-fun.git
