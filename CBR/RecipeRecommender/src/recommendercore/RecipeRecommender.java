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
import jcolibri.exception.AttributeAccessException;
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
	private static int casesToRetrieve = 3;
	
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
		Collection<CBRCase> selectedcases = SelectCases.selectTopK(eval, casesToRetrieve);
		
		Iterator<CBRCase> caseIterator = selectedcases.iterator();
		while(caseIterator.hasNext()){
			CBRCase caseInst=caseIterator.next();
			RecipeDescription component = (RecipeDescription) caseInst.getDescription();
		    System.out.println(component.getCuisineType());
		}
		return selectedcases;
	}
	
	public HashMap<String,List<CBRCase>> reuse(CBRQuery query,Collection<CBRCase> selectedCases){
		HashMap<String,List<CBRCase>> cbrMap=adaptCasesIfRequired(selectedCases,((RecipeDescription)query.getDescription()).getIngredients().split(","),query);
		System.out.println("*** After Case Adaptation Phase **");
		if(cbrMap.get("adapted")==null)
			System.out.println("** No cases Adapted **");
		else
			for(CBRCase adaptedComponent:cbrMap.get("adapted"))
				System.out.println(((RecipeDescription)adaptedComponent.getDescription()).getCuisineType()+"::"+((RecipeDescription)adaptedComponent.getDescription()).getIngredients());
		if(cbrMap.get("adapted").size()<casesToRetrieve)
			for(CBRCase unadaptedComponent:cbrMap.get("unadapted"))
				System.out.println(((RecipeDescription)unadaptedComponent.getDescription()).getCuisineType()+"::"+((RecipeDescription)unadaptedComponent.getDescription()).getIngredients());
		/*java.util.Collection<CBRCase> cases = _caseBase.getCases();
		for(CBRCase component: cases)
			System.out.println(((RecipeDescription)component.getDescription()).getCuisineType()+"::"+((RecipeDescription)component.getDescription()).getIngredients());*/
		return cbrMap;
	}
	
	public boolean adaptCase(CBRCase caseInput,CBRCase caseToAdapt,CBRQuery query){
		boolean adapted=false;
		RecipeDescription caseDesc = (RecipeDescription)caseInput.getDescription();
		RecipeDescription caseToAdaptDesc = (RecipeDescription)caseToAdapt.getDescription();
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
						//caseDesc.setIngredients(substituedIngredients);
						caseToAdaptDesc.setDishName(caseDesc.getDishName());
						caseToAdaptDesc.setIngredients(substituedIngredients);
						adapted=true;
					}
				}
				
			}
		}
		return adapted;
	}
	
	public HashMap<String,List<CBRCase>> adaptCasesIfRequired(Collection<CBRCase> selectedCases,
			String[] ingredients,CBRQuery query) {
		    HashMap<String,List<CBRCase>> cbrMap = new HashMap<String,List<CBRCase>>();
			ArrayList<CBRCase> unadaptedCases = new ArrayList<CBRCase>();
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
					CBRCase tempInst = new CBRCase();
					RecipeDescription desc = new RecipeDescription();
					tempInst.setDescription(desc);
					if(adaptCase(caseInst,tempInst,query)){
						adaptedCases.add(tempInst);
						System.out.println("**** Adapted Case ********");
						System.out.println("Recipe Name: "+((RecipeDescription)caseInst.getDescription()).getDishName()+"  "+((RecipeDescription)tempInst.getDescription()).getIngredients());
					}
					else{
						System.out.println("*** This Recipe "+((RecipeDescription)caseInst.getDescription()).getDishName()+" Cannot be adapted. No adaptable ingredients found in knowledge base ***");
						unadaptedCases.add(caseInst);
					}
				}
		    }
		    cbrMap.put("adapted", adaptedCases);
		    cbrMap.put("unadapted", unadaptedCases);
		    return cbrMap;
		    
	}

	public void revise(CBRQuery query,Collection<CBRCase> selectedCases,HashMap<String,List<CBRCase>> cbrMap){
		
		System.out.println("*** Selected Recipies Closely Matching Your Preference ***");
		int counter=0;
		if(cbrMap.get("adapted")!=null){
			System.out.println("** Cases Adapted According To Your Preference");
			for(CBRCase caseInst:cbrMap.get("adapted")){
				if(counter>=casesToRetrieve)
					break;
				RecipeDescription desc = (RecipeDescription)caseInst.getDescription();
				System.out.println("** DishName: "+desc.getDishName()+" "+"Ingredients: "+desc.getIngredients());
				System.out.println("**** Are you satisfied with the Recommendation?Please give a rating(ONE,TWO,THREE,FOUR,FIVE) ***");
				String rating=in.next();
				desc.setRatingScale(Rating.valueOf(rating));
				counter+=1;
			}
		}
		if(counter<casesToRetrieve){
				for(CBRCase caseInst:cbrMap.get("unadapted")){
					if(counter>=casesToRetrieve)
						break;
					RecipeDescription desc = (RecipeDescription)caseInst.getDescription();
					System.out.println("** DishName: "+desc.getDishName()+" "+"Ingredients: "+desc.getIngredients());
					System.out.println("**** Are you satisfied with the Recommendation?Please give a rating(ONE,TWO,THREE,FOUR,FIVE) ***");
					String rating=in.next();
					desc.setRatingScale(Rating.valueOf(rating));
					counter+=1;
				}
			}
		/*System.out.println("**** Are you satisfied with the solution?Please give a rating(ONE,TWO,THREE,FOUR,FIVE) ***");
		String rating=in.next();
		RecipeDescription desc=(RecipeDescription) query.getDescription();
		desc.setRatingScale(Rating.valueOf(rating));
		query.setDescription(desc);
		*/
		//DirectAttributeCopyMethod.copyAttribute(new Attribute("RatingScale",RecipeDescription.class), new Attribute("RatingScale",RecipeDescription.class), query, selectedCases);
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
		HashMap<String,List<CBRCase>> cbrMap=reuse(query,retrievedCases);
		
		//Revise
		revise(query,retrievedCases,cbrMap);
		
		//Retain
		try {
			retain(query,retrievedCases,cbrMap);
		} catch (AttributeAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void retain(CBRQuery query, Collection<CBRCase> retrievedCases,
			HashMap<String, List<CBRCase>> cbrMap) throws AttributeAccessException {
		int counter=0,idCounter=_caseBase.getCases().size();
		Collection<CBRCase> casesToAdd = new ArrayList<CBRCase>();
		if (cbrMap.get("adapted") != null) {
			for (CBRCase caseInst : cbrMap.get("adapted")) {
				if (counter >= casesToRetrieve)
					break;
				CBRCase newCase = new CBRCase();
				RecipeDescription desc = (RecipeDescription)caseInst.getDescription();
				if(desc.getRatingScale().equals(Rating.FOUR) || desc.getRatingScale().equals(Rating.FIVE)){
					System.out.println("*** Retaining AdaptedCase "+desc.getDishName()+"**");
					desc.setCaseId(idCounter+1);
					newCase.setDescription(desc);
					casesToAdd.add(newCase);
				}
				counter+=1;
				idCounter+=1;
			}
		}
		_caseBase.learnCases(casesToAdd);
		System.out.println("*** Final Cases In CaseBase ***");
		for(CBRCase caseTemp:_caseBase.getCases()){
			RecipeDescription desc = (RecipeDescription)caseTemp.getDescription();
			System.out.println("Dishname: "+desc.getDishName()+" Ingredients:"+desc.getIngredients());
		}
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
		_connector.close();
		jcolibri.test.database.HSQLDBserver.shutDown();
		
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
