package recommendercore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import jcolibri.casebase.CachedLinealCaseBase;
import jcolibri.casebase.LinealCaseBase;
import jcolibri.cbraplications.StandardCBRApplication;
import jcolibri.cbrcore.Attribute;
import jcolibri.cbrcore.CBRCase;
import jcolibri.cbrcore.CBRCaseBase;
import jcolibri.cbrcore.CBRQuery;
import jcolibri.cbrcore.Connector;
import jcolibri.connector.DataBaseConnector;
import jcolibri.exception.ExecutionException;
import jcolibri.method.retrieve.RetrievalResult;
import jcolibri.method.retrieve.NNretrieval.NNConfig;
import jcolibri.method.retrieve.NNretrieval.NNScoringMethod;
import jcolibri.method.retrieve.NNretrieval.similarity.global.Average;
import jcolibri.method.retrieve.NNretrieval.similarity.local.EnumDistance;
import jcolibri.method.retrieve.NNretrieval.similarity.local.Equal;
import jcolibri.method.retrieve.selection.SelectCases;
import jcolibri.method.reuse.DirectAttributeCopyMethod;
import recommendercore.RecipeDescription.CuisineType;
import recommendercore.RecipeDescription.DietType;
import recommendercore.RecipeDescription.MealType;
import recommendercore.RecipeDescription.Rating;

public class RecipeRecommender implements StandardCBRApplication {

	private static RecipeRecommender _instance = null;
	HashMap<String, ArrayList<String>> ingredientsMap = new HashMap<String,ArrayList<String>>();
	int adaptationTreshold=2;
	
	Scanner in = new Scanner(System.in);
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

    public void buildIngredientReplacementMap(){
    	ArrayList<String> tempList; 
    	tempList = new ArrayList<String>();
    	tempList.add("Cauliflower");
    	tempList.add("Cabbage");
    	ingredientsMap.put("Brocolli",tempList);
    	
    	tempList = new ArrayList<String>();
    	tempList.add("Raddish");
    	tempList.add("Beetroot");
    	ingredientsMap.put("Carrot",tempList);
    	
    	tempList = new ArrayList<String>();
    	tempList.add("Drumstick");
    	tempList.add("Beans");
    	ingredientsMap.put("Ladysfinger",tempList);
    	
    	tempList = new ArrayList<String>();
    	tempList.add("Elachi");
    	tempList.add("Mustard");
    	ingredientsMap.put("Clove",tempList);
    	
    }
	
	@Override
	public void configure() throws ExecutionException {
		try {
			
			//Temporarily building ingredients map
			buildIngredientReplacementMap();
			
			//Emulate data base server
			HSQLDBServer.init();
			
			// Create a data base connector
			_connector = new DataBaseConnector();
	
			// Init the ddbb connector with the config file
			_connector.initFromXMLfile(jcolibri.util.FileIO
					.findFile("/home/deva/workspace/luna/RecipeRecommender/src/recommendercore/databaseconfig.xml"));
		
			// Create a Lineal case base for in-memory organization
			_caseBase = new LinealCaseBase();
		}
		catch(Exception e){
			e.printStackTrace();
		}
   }
	
	public Collection<CBRCase> retrieve(CBRQuery query){
		NNConfig simConfig = getSimilarityConfig();
		simConfig.setDescriptionSimFunction(new Average());
		
		// Execute NN
		Collection<RetrievalResult> eval = NNScoringMethod.evaluateSimilarity(_caseBase.getCases(), query, simConfig);
		
		// Select k cases
		Collection<CBRCase> selectedcases = SelectCases.selectTopK(eval, 2);
		
		Iterator<CBRCase> caseIterator = selectedcases.iterator();
		while(caseIterator.hasNext()){
			CBRCase caseInst=caseIterator.next();
			RecipeDescription component = (RecipeDescription) caseInst.getDescription();
		    System.out.println(component.getCuisineType());
		}
		return selectedcases;
	}
	
	public void reuse(CBRQuery query,Collection<CBRCase> selectedCases){
		adaptatCasesIfRequired(selectedCases,((RecipeDescription)query.getDescription()).getIngredients().split(","),query);
	}
	
	public boolean adaptCase(CBRCase caseInput,CBRQuery query){
		boolean adapted=false;
		RecipeDescription caseDesc = (RecipeDescription)caseInput.getDescription();
		List<String> queryIngredientsList = Arrays.asList(((RecipeDescription)query.getDescription()).getIngredients().split(","));
		List<String> caseIngredientsList = Arrays.asList(caseDesc.getIngredients().split(","));
		HashSet<String> ingredientsToConsider = new HashSet<String>();
		for(String ing:queryIngredientsList){
			ingredientsToConsider.add(ing);
		}
		ingredientsToConsider.removeAll(caseIngredientsList);
		for(String ing:ingredientsToConsider){
			if(ingredientsMap.get(ing)!=null){
				List<String> substituteIng = ingredientsMap.get(ing);
				for(String tempSubstIng:substituteIng){
					if(caseIngredientsList.contains(tempSubstIng)){
						String substituedIngredients=caseDesc.getIngredients().replace(tempSubstIng,ing);
						caseDesc.setIngredients(substituedIngredients);
						adapted=true;
					}
				}
				
			}
		}
		return adapted;
	}
	
	public void adaptatCasesIfRequired(Collection<CBRCase> selectedCases,
			String[] ingredients,CBRQuery query) {
			ArrayList<CBRCase> adaptedCases = new ArrayList<CBRCase>(); 
		    for(CBRCase caseInst:selectedCases){
		    	int counter=0;
				for(String ing:ingredients){
					String[] ingFromCase=((RecipeDescription)caseInst.getDescription()).getIngredients().split(",");
					if(Arrays.asList(ingFromCase).contains(ing)){
						counter+=1;
					}
				}
				if(counter<adaptationTreshold){
					if(adaptCase(caseInst,query)){
						adaptedCases.add(caseInst);
						System.out.println("**** Adapted Case ********");
						System.out.println(((RecipeDescription)caseInst.getDescription()).getIngredients());
					}
				}
		    }
	}

	public void revise(CBRQuery query,Collection<CBRCase> selectedCases){
		System.out.println("**** Are you satisfied with the solution?Please give a rating(ONE,TWO,THREE,FOUR,FIVE) ***");
		String rating=in.next();
		RecipeDescription desc=(RecipeDescription) query.getDescription();
		desc.setRatingScale(Rating.valueOf(rating));
		query.setDescription(desc);
		
		DirectAttributeCopyMethod.copyAttribute(new Attribute("RatingScale",RecipeDescription.class), new Attribute("RatingScale",RecipeDescription.class), query, selectedCases);
		System.out.println("*** Revised Cases ***");
		java.util.Collection<CBRCase> cases = _caseBase.getCases();
		for(CBRCase component: cases)
			System.out.println(((RecipeDescription)component.getDescription()).getCuisineType()+"::"+((RecipeDescription)component.getDescription()).getRatingScale());
	}
	
	@Override
	public void cycle(CBRQuery query) throws ExecutionException {
		
		//Retrieve
		Collection<CBRCase> retrievedCases = retrieve(query);
		
		//Reuse
		reuse(query,retrievedCases);
		
		//Revise
		revise(query,retrievedCases);

	}

	private NNConfig getSimilarityConfig() {
		NNConfig config = new NNConfig();
		Attribute attribute;

		attribute = new Attribute("CuisineType",RecipeDescription.class);
		config.addMapping(attribute, new Equal());
		config.setWeight(attribute, 1.0);
		
		attribute = new Attribute("DietType",RecipeDescription.class);
		config.addMapping(attribute, new Equal());
		config.setWeight(attribute, 1.0);
		
		attribute = new Attribute("MealType",RecipeDescription.class);
		config.addMapping(attribute, new Equal());
		config.setWeight(attribute, 1.0);
		
		
		attribute = new Attribute("RatingScale",RecipeDescription.class);
		config.addMapping(attribute, new EnumDistance());
		config.setWeight(attribute, 1.0);
		
		attribute = new Attribute("ingredients",RecipeDescription.class);
		config.addMapping(attribute, new TokensContained());
		config.setWeight(attribute, 1.0);

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
		description.setCuisineType(CuisineType.valueOf("Thai"));
		description.setDietType(DietType.valueOf("Vegetarian"));
		description.setMealType(MealType.valueOf("Breakfast"));
		description.setIngredients("Beans,Carrot");
		description.setRatingScale(Rating.valueOf("FIVE"));
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
