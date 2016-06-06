package recommendercore;

import jcolibri.cbrcore.Attribute;
import jcolibri.cbrcore.CaseComponent;



public class RecipeDescription  implements CaseComponent {
	
	public enum CuisineType {Indian,Malay,Thai,European}
    public enum DietType {Vegetarian,NonVegetarian}
    public enum MealType {Breakfast,Lunch,Dinner}
    public enum Rating {ONE,TWO,THREE,FOUR,FIVE}
	
    int caseId;
    CuisineType CuisineType;
    DietType DietType;
    MealType MealType;
    String method="dummy";
    String ingredients;
    String dishName;
    Rating RatingScale;
       
	public Rating getRatingScale() {
		return RatingScale;
	}

	public void setRatingScale(Rating ratingScale) {
		RatingScale = ratingScale;
	}

	public String getDishName() {
		return dishName;
	}

	public void setDishName(String dishName) {
		this.dishName = dishName;
	}

	public String getIngredients() {
		return ingredients;
	}

	public void setIngredients(String ingredients) {
		this.ingredients = ingredients;
	}

	public int getCaseId() {
		return caseId;
	}

	public void setCaseId(int caseId) {
		this.caseId = caseId;
	}

	public CuisineType getCuisineType() {
		return CuisineType;
	}

	public void setCuisineType(CuisineType cuisine) {
		this.CuisineType = cuisine;
	}

	public DietType getDietType() {
		return DietType;
	}

	public void setDietType(DietType dietType) {
		this.DietType = dietType;
	}

	public MealType getMealType() {
		return MealType;
	}

	public void setMealType(MealType mealType) {
		this.MealType = mealType;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}
	
	@Override
	public Attribute getIdAttribute() {
		// TODO Auto-generated method stub
		return new Attribute("caseId", this.getClass());
	}

}
