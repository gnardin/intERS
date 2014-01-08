###
### Required libraries
###
library(data.table)
library(ggplot2)
library(gridExtra)
library(bigmemory)
library(biganalytics)
library(bigtabulate)
library(doMC)
registerDoMC(3)

###
### Raw simulation data output directory
###
outputPath <- "/data/workspace/repast/intERS/output/15-enlarge-20/homogeneous"

###
### Images repository directory
###
dir.create(file.path(outputPath, "images"), showWarnings = FALSE)
imagePath <- paste(outputPath,"/images", sep="")

###
### Consolidate the Raw Simulation data
###
numRuns <- 3                               # Number of simulation runs

e <- NULL
t <- NULL
for(combination in combinations){
  for(run in seq(1:numRuns)){
    
    extorter <- read.csv(paste(outputPath,"/",run,"/extorters.csv",sep=""),
                         sep=";", header=TRUE)
    extorter <- data.table(extorter)
    setkey(extorter, cycle)
    lastCycle = max(extorter[,"cycle"])
    e <- rbind(e, extorter[which(cycle==lastCycle),list(type,wealth,numTargets)])
    
    target <- read.csv(paste(outputPath,"/",run,"/targets.csv",sep=""),
                       sep=";", header=TRUE)
    target <- data.table(target)
    setkey(target, cycle)
    lastCycle = max(target[,"cycle"])
    t <- rbind(t, target[which(cycle==lastCycle),list(type=combination,wealth)])
  }
}

write.table(e,file=paste(outputPath,"/cDistExtorters.csv",sep=""), sep=";",
            quote=FALSE, col.names=TRUE, row.names=FALSE)

write.table(t,file=paste(outputPath,"/cDistTargets.csv",sep=""), sep=";",
            quote=FALSE, col.names=TRUE, row.names=FALSE)

###
### Upload Consolidated Information
###
e <- data.table(read.csv(paste(outputPath,"/cDistExtorters.csv",sep=""),sep=";"))
setkey(e, type)

t <- data.table(read.csv(paste(outputPath,"/cDistTargets.csv",sep=""),sep=";"))

###
### WEALTH DISTRIBUTION
###
minWealthX <- floor(min(e[,wealth]))
maxWealthX <- ceiling(max(e[,wealth]))

minY <- 0
maxY <- 35

plotLL <- qplot(wealth, data=e[which(type=="LL")], geom = "histogram",
                xlim = c(minWealthX, maxWealthX), ylim = c(minY,maxY),
                main="LL", xlab="Wealth", ylab="Number of Extorters")
plotLH <- qplot(wealth, data=e[which(type=="LH")], geom = "histogram",
                xlim = c(minWealthX, maxWealthX), ylim = c(minY,maxY),
                main="LH", xlab="Wealth", ylab="Number of Extorters")
plotHL <- qplot(wealth, data=e[which(type=="HL")], geom = "histogram",
                xlim = c(minWealthX, maxWealthX), ylim = c(minY,maxY),
                main="HL", xlab="Wealth", ylab="Number of Extorters")
plotHH <- qplot(wealth, data=e[which(type=="HH")], geom = "histogram",
                xlim = c(minWealthX, maxWealthX), ylim = c(minY,maxY),
                main="HH", xlab="Wealth", ylab="Number of Extorters")
png(file=paste(imagePath,"/distributionExtorterWealth-Histogram.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Extorters Wealth Distribution")
dev.off()

###
### BOXPLOT WEALTH DISTRIBUTION
###
png(file=paste(imagePath,"/distributionExtorterWealth-Boxplot.png", sep=""), width=800, height=600)
qplot(type, wealth, data=e, geom = "boxplot",
      main = "Extorters Wealth Distribution")
dev.off()

###
### JITTER WEALTH DISTRIBUTION
###
png(file=paste(imagePath,"/distributionExtorterWealth-Jitter.png", sep=""), width=800, height=600)
qplot(type, wealth, data=e, geom = "jitter",
      main = "Extorters Wealth Distribution", colour=factor(type))
dev.off()


###
### NUMBER OF TARGETS DISTRIBUTION
###
minWealthX <- floor(min(e[,numTargets]))
maxWealthX <- ceiling(max(e[,numTargets]))

minY <- 0
maxY <- 35

plotLL <- qplot(numTargets, data=e[which(type=="LL")], geom = "histogram",
                xlim = c(minWealthX, maxWealthX), ylim = c(minY,maxY),
                main="LL", xlab="Number of Targets", ylab="Number of Extorters")
plotLH <- qplot(numTargets, data=e[which(type=="LH")], geom = "histogram",
                xlim = c(minWealthX, maxWealthX), ylim = c(minY,maxY),
                main="LH", xlab="Number of Targets", ylab="Number of Extorters")
plotHL <- qplot(numTargets, data=e[which(type=="HL")], geom = "histogram",
                xlim = c(minWealthX, maxWealthX), ylim = c(minY,maxY),
                main="HL", xlab="Number of Targets", ylab="Number of Extorters")
plotHH <- qplot(numTargets, data=e[which(type=="HH")], geom = "histogram",
                xlim = c(minWealthX, maxWealthX), ylim = c(minY,maxY),
                main="HH", xlab="Number of Targets", ylab="Number of Extorters")
png(file=paste(imagePath,"/distributionExtorterNumberTargets-Histogram.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Number of Targets Distribution")
dev.off()

###
### BOXPLOT NUMBER OF TARGETS DISTRIBUTION
###
png(file=paste(imagePath,"/distributionExtorterNumberTargets-Boxplot.png", sep=""), width=800, height=600)
qplot(type, numTargets, data=e, geom = "boxplot",
      main = "Extorters Number of Targets Distribution")
dev.off()

###
### JITTER NUMBER OF TARGETS DISTRIBUTION
###
png(file=paste(imagePath,"/distributionExtorterNumberTargets-Jitter.png", sep=""), width=800, height=600)
qplot(type, numTargets, data=e, geom = "jitter",
      main = "Extorters Number of Targets Distribution", colour=factor(type))
dev.off()


###
### TARGETS WEALTH DISTRIBUTION
###
minWealthX <- floor(min(t[,wealth]))
maxWealthX <- ceiling(max(t[,wealth]))

minY <- 0
maxY <- 300

plotLL <- qplot(wealth, data=t[which(type=="LL")], geom = "histogram",
                xlim = c(minWealthX, maxWealthX), ylim = c(minY,maxY),
                main="LL", xlab="Wealth", ylab="Number of Targets")
plotLH <- qplot(wealth, data=t[which(type=="LH")], geom = "histogram",
                xlim = c(minWealthX, maxWealthX), ylim = c(minY,maxY),
                main="LH", xlab="Wealth", ylab="Number of Targets")
plotHL <- qplot(wealth, data=t[which(type=="HL")], geom = "histogram",
                xlim = c(minWealthX, maxWealthX), ylim = c(minY,maxY),
                main="HL", xlab="Wealth", ylab="Number of Targets")
plotHH <- qplot(wealth, data=t[which(type=="HH")], geom = "histogram",
                xlim = c(minWealthX, maxWealthX), ylim = c(minY,maxY),
                main="HH", xlab="Wealth", ylab="Number of Targets")
png(file=paste(imagePath,"/distributionTargetsWealth-Histogram.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Number of Targets Distribution")
dev.off()

###
### BOXPLOT TARGETS WEALTH DISTRIBUTION
###
png(file=paste(imagePath,"/distributionTargetsWealth-Boxplot.png", sep=""), width=800, height=600)
qplot(type, wealth, data=t, geom = "boxplot",
      main = "Targets Wealth Distribution")
dev.off()

###
### JITTER TARGETS WEALTH DISTRIBUTION
###
png(file=paste(imagePath,"/distributionTargetsWealth-Jitter.png", sep=""), width=800, height=600)
qplot(type, wealth, data=t, geom = "jitter",
      main = "Targets Wealth Distribution", colour=factor(type))
dev.off()