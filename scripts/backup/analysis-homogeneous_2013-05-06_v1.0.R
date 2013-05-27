###
### Required libraries
###
library(data.table)

###
### Raw simulation data output directory
###
outputPath <- "/data/workspace/repast/intERS/output/28-enlarge-20-counterattack-20-tolerance-40/homogeneous"

###
### Consolidate the Raw Simulation data
###
numRuns <- 30                              # Number of simulation runs
combinations <- c(0,1,2,3)                 # Combination numbers
dirNames <- c("LL","LH","HL","HH")         # Directories names

for(combination in combinations){

  for(run in seq(1:numRuns)){
    
    if(run > 1){
      writeColLabels <- FALSE
      appendData <- TRUE
    } else {
      writeColLabels <- TRUE
      appendData <- FALSE
    }
    
    extorter <- read.csv(paste(outputPath,"/",dirNames[combination + 1],"/",run,"/extorters.csv",sep=""),
                         sep=";", header=TRUE)
    extorter <- data.table(extorter)
    setkey(extorter, cycle)
    extorter <- extorter[,which(!grepl("type", colnames(extorter))), with = FALSE]
    extorterAvg <- extorter[, lapply(.SD,mean), by=cycle]
    extorterSum <- extorter[, lapply(.SD,sum), by=cycle]
    
    observer <- read.csv(paste(outputPath,"/",dirNames[combination + 1],"/",run,"/observers.csv",sep=""),
                         sep=";", header=TRUE)
    observer <- data.table(observer)
    setkey(observer, cycle)
    observer <- observer[,which(!grepl("type", colnames(observer))), with = FALSE]
    observerAvg <- observer[, lapply(.SD,mean), by=cycle]
    observerSum <- observer[, lapply(.SD,sum), by=cycle]
    
    target <- read.csv(paste(outputPath,"/",dirNames[combination + 1],"/",run,"/targets.csv",sep=""),
                       sep=";", header=TRUE)
    target <- data.table(target)
    setkey(target, cycle)
    target <- target[,which(!grepl("type", colnames(target))), with = FALSE]
    targetAvg <- target[, lapply(.SD,mean), by=cycle]
    targetSum <- target[, lapply(.SD,sum), by=cycle]
    
    write.table(extorterAvg,file=paste(outputPath,"/","cAvgExtorters",dirNames[combination + 1],".csv",sep=""),
                sep=";",quote=FALSE,col.names=writeColLabels,row.names=FALSE,append=appendData)
    
    write.table(extorterSum,file=paste(outputPath,"/","cSumExtorters",dirNames[combination + 1],".csv",sep=""),
                sep=";",quote=FALSE,col.names=writeColLabels,row.names=FALSE,append=appendData)
    
    write.table(observerAvg,file=paste(outputPath,"/","cAvgObservers",dirNames[combination + 1],".csv",sep=""),
                sep=";",quote=FALSE,col.names=writeColLabels,row.names=FALSE,append=appendData)
    
    write.table(observerSum,file=paste(outputPath,"/","cSumObservers",dirNames[combination + 1],".csv",sep=""),
                sep=";",quote=FALSE,col.names=writeColLabels,row.names=FALSE,append=appendData)
    
    write.table(targetAvg,file=paste(outputPath,"/","cAvgTargets",dirNames[combination + 1],".csv",sep=""),
                sep=";",quote=FALSE,col.names=writeColLabels,row.names=FALSE,append=appendData)
    
    write.table(targetSum,file=paste(outputPath,"/","cSumTargets",dirNames[combination + 1],".csv",sep=""),
                sep=";",quote=FALSE,col.names=writeColLabels,row.names=FALSE,append=appendData)
  }
}

extorterDist <- NULL
targetDist <- NULL
for(combination in combinations){
  for(run in seq(1:numRuns)){
    
    extorter <- read.csv(paste(outputPath,"/",dirNames[combination + 1],"/",run,"/extorters.csv",sep=""),
                         sep=";", header=TRUE)
    extorter <- data.table(extorter)
    setkey(extorter, cycle)
    lastCycle = max(as.data.frame(extorter)[,"cycle"])
    extorterDist <- rbind(extorterDist, extorter[which(cycle==lastCycle),list(type,wealth,numTargets)])
    
    target <- read.csv(paste(outputPath,"/",dirNames[combination + 1],"/",run,"/targets.csv",sep=""),
                       sep=";", header=TRUE)
    target <- data.table(target)
    setkey(target, cycle)
    lastCycle = max(as.data.frame(target)[,"cycle"])
    targetDist <- rbind(targetDist, target[which(cycle==lastCycle),list(type=combination,wealth)])
  }
}

write.table(extorterDist,file=paste(outputPath,"/cDistExtorters.csv",sep=""), sep=";",
            quote=FALSE, col.names=TRUE, row.names=FALSE)

write.table(targetDist,file=paste(outputPath,"/cDistTargets.csv",sep=""), sep=";",
            quote=FALSE, col.names=TRUE, row.names=FALSE)