

webaccessdata<-read.csv("/home/deva/workspace/CA/sem2/webanalytics/MSFT-vroots/customeraccess.csv",header=FALSE)

transactiondf <- data.frame(userid=c(),vrootid=c())


tempUserid=0

for(rowNum in 1:nrow(webaccessdata)){

  if(webaccessdata[rowNum,1]=="C"){
    tempUserid=webaccessdata[rowNum,3]
  }
  else{
    transactiondf<-rbind(transactiondf,c(userid=tempUserid,vrootid=webaccessdata[rowNum,2]))
  }
  print(rowNum)
}
names(transactiondf) <- c("userid","vrootid")
write.csv(transactiondf,"/home/deva/workspace/CA/sem2/webanalytics/MSFT-vroots/webtransactions.csv",row.names=FALSE)
