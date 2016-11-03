library(plyr)
library(arulesSequences)

######## Preprocessing  ##############
webtransactions <- read.csv("/home/deva/workspace/CA/sem2/webanalytics/MSFT-vroots/webtransactions.csv")

genEventId<-function(x){
  x$eventid<-seq(1:nrow(x))
  x$size<-1
  return(x)
}

webseqdata<-rbind(ddply(webtransactions,.(userid),genEventId))

write.table(webseqdata[,c(1,3,4,2)],"/home/deva/workspace/CA/sem2/webanalytics/MSFT-vroots/websequence.csv",row.names = FALSE,col.names = FALSE,sep=",")


vroots <- read.csv("/home/deva/workspace/CA/sem2/webanalytics/MSFT-vroots/anonymous-vrootnames-msweb.csv")
names(vroots)<-c("vrootid","title","url")
mergeddf<-merge(webtransactions,vroots,al.x=TRUE)
mergeddf<-mergeddf[,c(2,1,3,4)]
################ Generate Rules from mined data ###############33

webtransactions<-read.csv("/home/deva/workspace/CA/sem2/webanalytics/MSFT-vroots/webtransactions.csv")
seqdata<-read_baskets(con="/home/deva/workspace/CA/sem2/webanalytics/MSFT-vroots/websequence.csv",sep=",",info=c("sequenceID","eventID","SIZE"))
as(head(seqdata), "data.frame") 

seqs <- cspade(seqdata, parameter = list(support = 0.01), control = list(verbose = TRUE))
as(seqs,"data.frame")  # view the sequences

rules <- ruleInduction(seqs, confidence = 0.1,control = list(verbose = TRUE))
inspect(rules)

sortedrules<-sort(rules,decreasing = TRUE,by="confidence")
rulesDF<-as(sortedrules[1:10,],"data.frame")


#getlhsandrhs<-function(rule,pattern){
#  chartoremove<-c("\\{","\\}","<",">")
#  rule<-gsub("=>","-",rule)
#  for(char in chartoremove){
#    rule<-gsub(char,"",rule)
#  }
#  ifelse(pattern==1,trimws(strsplit(rule,"-")[[1]][1]),trimws(strsplit(rule,"-")[[1]][2]))
#}

getlhsandrhs<-function(x){
  chartoremove<-c("\\{","\\}","<",">")
  rule<-gsub("=>","-",x$rule)
  for(char in chartoremove){
    rule<-gsub(char,"",rule)
  }
  x$lhs<-trimws(strsplit(rule,"-")[[1]][1])
  x$rhs<-trimws(strsplit(rule,"-")[[1]][2])
  return(x)
}

refinetransactions<-function(x){
  userid<-x$userid[1]
  pages<-paste0(x$vrootid,collapse = ",")
  return(data.frame(userid=userid,visitedpages=as.character(pages)))
}

checkrules<-function(x,userid,visitedpages){
 visitedpagesvec<-strsplit(visitedpages,",")[[1]]
 lhs<-strsplit(x$lhs,",")[[1]]
 rhs<-strsplit(x$rhs,",")[[1]]
 if(all(lhs%in%visitedpagesvec) && all(rhs%in%visitedpagesvec)){
    lhspositions<-match(lhs,visitedpagesvec)
    # cat("\nlhs --> ",lhs)
    # cat("\nrhs --> ",rhs)
    # cat("\nvisitedpages --> ",visitedpagesvec)
    # cat("\nlhspositions --> ",lhspositions)
    # cat("\nrhspositions --> ",match(rhs,visitedpagesvec))
    # cat("\nRulecheck",all(match(rhs,visitedpagesvec)>lhspositions))
    ifelse(all(match(rhs,visitedpagesvec)>lhspositions),return(data.frame(rule=x$rule[1],visitedpages=visitedpages,result=1)),return(data.frame(rule=x$rule[1],visitedpages=visitedpages,result=0))) 
  }else{
   return(data.frame(rule=x$rule[1],visitedpages=visitedpages,result=NA))
  }
 }


applyrulecheck<-function(x,rulesdf){
#print(x$visitedpages[1])
  ddply(rulesdf,.(rule),checkrules,x$userid[1],x$visitedpages[1])
}


rulesDF<-rbind(ddply(rulesDF,.(rule),getlhsandrhs))
refinedtransactiondf<-ddply(webtransactions,.(userid),refinetransactions)
refinedtransactiondf$visitedpages<-sapply(refinedtransactiondf$visitedpages,as.character)
resultsdf<-ddply(refinedtransactiondf,.(userid),applyrulecheck,rulesDF)
refinedresults<-na.omit(resultsdf)

cat("Total Correct Predictions:",paste0(nrow(refinedresults[refinedresults$result==1,]),"/",nrow(refinedresults)),nrow(refinedresults[refinedresults$result==1,])/nrow(refinedresults))






