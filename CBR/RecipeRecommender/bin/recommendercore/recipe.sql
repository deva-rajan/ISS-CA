create database recipe;
use recipe;
drop table recipe;
create table recipe(caseId INTEGER,CuisineType VARCHAR(15),DietType VARCHAR(15),MealType VARCHAR(15),ingredients VARCHAR(100),dishName VARCHAR(50),method VARCHAR(50));
insert into recipe(caseId,CuisineType,DietType,MealType,ingredients,dishName,method) values(1,'Indian','Vegetarian','Breakfast','Tomato,Onion,Garlic','Upma','dummy');
insert into recipe(caseId,CuisineType,DietType,MealType,ingredients,dishName,method) values(2,'Malay','NonVegetarian','Breakfast','Tomato,Fish,Chicken','Malala','dummy');
insert into recipe(caseId,CuisineType,DietType,MealType,ingredients,dishName,method) values(3,'Thai','Vegetarian','Breakfast','Tomato,Beans,Potato','ThaiSalad','dummy');
insert into recipe(caseId,CuisineType,DietType,MealType,ingredients,dishName,method) values(4,'European','NonVegetarian','Breakfast','Tomato,Cheese,Chicken','ChickenSalad','dummy');