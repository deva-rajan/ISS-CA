
library(arules)

transactiondf <- read.csv("/home/deva/workspace/CA/sem2/webanalytics/MSFT-vroots/webtransactions.csv")

webtransactions<- read.transactions("/home/deva/workspace/CA/sem2/webanalytics/MSFT-vroots/webtransactions.csv",format="single",sep=",",cols=c("userid","vrootid"))

rules<-apriori(webtransactions,parameter=list(supp=0.01,conf=0.9,target="rules"))

# a useful plot of training data
itemFrequencyPlot(webtransactions,topN=50,type="absolute")

