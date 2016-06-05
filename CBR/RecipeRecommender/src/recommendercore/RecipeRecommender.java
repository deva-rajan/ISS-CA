package recommendercore;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Scanner;

import jcolibri.casebase.CachedLinealCaseBase;
import jcolibri.casebase.LinealCaseBase;
import jcolibri.cbraplications.StandardCBRApplication;
import jcolibri.cbrcore.Attribute;
import jcolibri.cbrcore.CBRCase;
import jcolibri.cbrcore.CBRCaseBase;
import jcolibri.cbrcore.CBRQuery;
import jcolibri.cbrcore.CaseComponent;
import jcolibri.cbrcore.Connector;
import jcolibri.connector.DataBaseConnector;
import jcolibri.connector.PlainTextConnector;
import jcolibri.exception.ExecutionException;
import jcolibri.method.retrieve.RetrievalResult;
import jcolibri.method.retrieve.NNretrieval.NNConfig;
import jcolibri.method.retrieve.NNretrieval.NNScoringMethod;
import jcolibri.method.retrieve.NNretrieval.similarity.LocalSimilarityFunction;
import jcolibri.method.retrieve.NNretrieval.similarity.global.Average;
import jcolibri.method.retrieve.NNretrieval.similarity.local.Equal;
import jcolibri.method.retrieve.selection.SelectCases;
import jcolibri.test.test13.similarity.TokensContained;
import recommendercore.RecipeDescription.CuisineType;
import recommendercore.RecipeDescription.DietType;
import recommendercore.RecipeDescription.MealType;

public class RecipeRecommender implements StandardCBRApplication {

	private static RecipeRecommender _instance = null;
	public  static RecipeRecommender getInstance()
	{
		if(_instance == null)
		   _instance = new RecipeRecommender();
		return _instance;
	}
	
	private RecipeRecommender()
	{
	}
	
	/** Connector object */
	Connector _connector;
	/** CaseBase object */
	CBRCaseBase _caseBase = new CachedLinealCaseBase();

//	public ArrayList<CBRCase> getCaseList(){
//		 //Create Dummy Case Descriptions
//		RecipeDescription desc1 = new RecipeDescription();
//		desc1.setCaseId(1);
//		desc1.setDishName("Upma");
//		desc1.setCuisineType(CuisineType.Indian);
//		desc1.setDietType(DietType.Vegetarian);
//		desc1.setIngredients(new String[]{"Tomato","Garlic","Onion"});
//		
//		RecipeDescription desc2 = new RecipeDescription();
//		desc2.setCaseId(2);
//		desc2.setDishName("Malala");
//		desc2.setCuisineType(CuisineType.Malay);
//		desc2.setDietType(DietType.NonVegetarian);
//		desc2.setIngredients(new String[]{"Tomato","Fish","Chicken"});
//		
//		RecipeDescription desc3 = new RecipeDescription();
//		desc3.setCaseId(3);
//		desc3.setDishName("ThaiSalad");
//		desc3.setCuisineType(CuisineType.Thai);
//		desc3.setDietType(DietType.Vegetarian);
//		desc3.setIngredients(new String[]{"Tomato","Beans","Potato"});
//		
//		RecipeDescription desc4 = new RecipeDescription();
//		desc4.setCaseId(4);
//		desc4.setDishName("ChickenSalad");
//		desc4.setCuisineType(CuisineType.European);
//		desc4.setDietType(DietType.NonVegetarian);
//		desc4.setIngredients(new String[]{"Tomato","Cheese","Chicken"});
//				
//		//Create Cases From Descriptions
//		CBRCase case1 = new CBRCase();
//		case1.setDescription(desc1);
//		
//		CBRCase case2 = new CBRCase();
//		case2.setDescription(desc2);
//		
//		CBRCase case3 = new CBRCase();
//		case3.setDescription(desc3);
//		
//		CBRCase case4 = new CBRCase();
//		case4.setDescription(desc4);
//		
//		//Add cases into casebase
//		ArrayList<CBRCase> caseList = new ArrayList<CBRCase>();
//		caseList.add(case1);
//		caseList.add(case2);
//		caseList.add(case3);
//		caseList.add(case4);
//		
//		return caseList;
//	}
	
	@Override
	public void configure() throws ExecutionException {
		try {
			//Emulate data base server
			HSQLDBServer.init();
			
			// Create a data base connector
			_connector = new DataBaseConnector();
			
			//_connector = new PlainTextConnector();

			
			// Init the ddbb connector with the config file
			_connector.initFromXMLfile(jcolibri.util.FileIO
					.findFile("/home/deva/workspace/luna/RecipeRecommender/src/recommendercore/databaseconfig.xml"));
			/*_connector.initFromXMLfile(jcolibri.util.FileIO
					.findFile("/home/deva/workspace/luna/RecipeRecommender/src/recommendercore/plaintextconfig.xml"));*/
			
			// Create a Lineal case base for in-memory organization
			_caseBase = new LinealCaseBase();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	//_caseBase.learnCases(getCaseList());
    }
	
	
	
	

	@Override
	public void cycle(CBRQuery query) throws ExecutionException {
		NNConfig simConfig = getSimilarityConfig();
		simConfig.setDescriptionSimFunction(new Average());
		
		// Execute NN
		Collection<RetrievalResult> eval = NNScoringMethod.evaluateSimilarity(_caseBase.getCases(), query, simConfig);
		
		// Select k cases
		Collection<CBRCase> selectedcases = SelectCases.selectTopK(eval, 5);
		
		Iterator<CBRCase> caseIterator = selectedcases.iterator();
		while(caseIterator.hasNext()){
			CBRCase caseInst=caseIterator.next();
			RecipeDescription component = (RecipeDescription) caseInst.getDescription();
		    System.out.println(component.getCuisineType());
		}
		
		
	}

	private NNConfig getSimilarityConfig() {
		NNConfig config = new NNConfig();
		Attribute attribute;
		LocalSimilarityFunction function;
		
	
		
		attribute = new Attribute("CuisineType",RecipeDescription.class);
		config.addMapping(attribute, new Equal());
		config.setWeight(attribute, 1.0);
		
		attribute = new Attribute("DietType",RecipeDescription.class);
		config.addMapping(attribute, new Equal());
		config.setWeight(attribute, 1.0);
		
		attribute = new Attribute("MealType",RecipeDescription.class);
		config.addMapping(attribute, new Equal());
		config.setWeight(attribute, 1.0);
		
		attribute = new Attribute("ingredients",RecipeDescription.class);
		config.addMapping(attribute, new TokensContained());
		config.setWeight(attribute, 8.2);
		

		return config;
	}

	@Override
	public void postCycle() throws ExecutionException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public CBRCaseBase preCycle() throws ExecutionException {
		// Load cases from connector into the case base
		_caseBase.init(_connector);		
		// Print the cases
		java.util.Collection<CBRCase> cases = _caseBase.getCases();
		for(CBRCase c: cases)
			System.out.println(c);
		return _caseBase;
	}
	
	public CBRQuery getUserInput(){
		RecipeDescription description = new RecipeDescription();
		Scanner scanner = new Scanner(System.in);
		//System.out.println("*********************** Kindly Enter Your Preferences **************************");
		//System.out.println("Cuisine Preference(Indian/Malay/Thai/European): ");
		description.setCuisineType(CuisineType.valueOf("European"));
		//System.out.println("DietType(Vegetarian/NonVegetarian): ");
		description.setDietType(DietType.valueOf("NonVegetarian"));
		//System.out.println("MealType(Breakfast/Lunch/Dinner): ");
		description.setMealType(MealType.valueOf("Breakfast"));
		//System.out.println("PreferredIngredients(Carrot,Brocolli,Onion,Garlic,Bread,Cheese): ");
		description.setIngredients("Tomato,Onion");
		CBRQuery query = new CBRQuery();
		query.setDescription(description);
		return query;
	}

	public static void main(String[] args) {
		RecipeRecommender recipeRecommender = getInstance();
		
		try {
			recipeRecommender.configure();
			recipeRecommender.preCycle();
			CBRQuery query=recipeRecommender.getUserInput();
			recipeRecommender.cycle(query);
			
			
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}
