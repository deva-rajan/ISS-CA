create database recipe;
use recipe;
drop table recipe;
create table recipe(caseId INTEGER,CuisineType VARCHAR(15),DietType VARCHAR(15),MealType VARCHAR(15),ingredients VARCHAR(100),dishName VARCHAR(50),Rating VARCHAR(30),method VARCHAR(50));
insert into recipe(caseId,CuisineType,DietType,MealType,ingredients,dishName,Rating,method) values(1,'Indian','Vegetarian','Breakfast','Tomato,Onion,Garlic','Upma','FIVE','dummy');
insert into recipe(caseId,CuisineType,DietType,MealType,ingredients,dishName,Rating,method) values(2,'Malay','NonVegetarian','Breakfast','Tomato,Fish,Chicken','Malala','FIVE','dummy');
insert into recipe(caseId,CuisineType,DietType,MealType,ingredients,dishName,Rating,method) values(3,'Thai','Vegetarian','Breakfast','Tomato,Raddish,Beans,Potato','ThaiSalad','FIVE','dummy');
insert into recipe(caseId,CuisineType,DietType,MealType,ingredients,dishName,Rating,method) values(4,'European','NonVegetarian','Breakfast','Tomato,Cheese,Chicken','ChickenSalad','FIVE','dummy');