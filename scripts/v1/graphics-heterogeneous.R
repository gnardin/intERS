###
### Required libraries
###
library(data.table)
library(ggplot2)
library(gridExtra)

###
### Command Arguments
###
args <- commandArgs(TRUE)

###
### Raw simulation data output directory
###
en <- as.numeric(args[1])
to <- as.numeric(args[2])
ex <- as.numeric(args[3])
pu <- as.numeric(args[4])

###
### Comment all those lines when using with Rscript
###
rm(list=ls())
en <- 10
to <- 40
ex <- 10
pu <- 40

###
### Raw simulation data output directory
###
basePath <- "/data/workspace/gloders/intERS/output"

outputPath <- paste(basePath, "/withoutProtection.punish.noRunaway-15000", sep="")
#outputPath <- paste(basePath,"/En",en,"_To",to,
#                    "/Ex",ex,"-",(ex*2),"_Pu",pu,"-",(pu*2),"_En",en,"_To",to,
#                    "/LL-LH-HL-HH", sep="")
        
###
### Images repository directory
###
dir.create(file.path(outputPath, "images"), showWarnings = FALSE)
imagePath <- paste(outputPath,"/images", sep="")

###
### Upload Consolidated Information
###
extorterSum <- data.table(read.csv(paste(outputPath,"/cSumextorters.csv",sep=""),sep=";"))
setkey(extorterSum, type, cycle)

extorterAvg <- data.table(read.csv(paste(outputPath,"/cAvgextorters.csv",sep=""),sep=";"))
setkey(extorterAvg, type, cycle)

observerSum <- data.table(read.csv(paste(outputPath,"/","cSumobservers.csv",sep=""), sep=";"))
setkey(observerSum, type, cycle)

observerAvg <- data.table(read.csv(paste(outputPath,"/","cAvgobservers.csv",sep=""), sep=";"))
setkey(observerAvg, type, cycle)

targetSum <- data.table(read.csv(paste(outputPath,"/","cSumtargets.csv",sep=""), sep=";"))
setkey(targetSum, type, cycle)

targetAvg <- data.table(read.csv(paste(outputPath,"/","cAvgtargets.csv",sep=""), sep=";"))
setkey(targetAvg, type, cycle)

###
### INDEX NUMBER OF EXTORTERS < 1
###
iLL <- as.numeric(min((1:NROW(observerAvg))[(observerAvg$FR0 +
                                               observerAvg$IM0) < 1],
                      NROW(observerAvg)+1))
iLH <- as.numeric(min((1:NROW(observerAvg))[(observerAvg$FR1 +
                                               observerAvg$IM1) < 1],
                      NROW(observerAvg)+1))
iHL <- as.numeric(min((1:NROW(observerAvg))[(observerAvg$FR2 +
                                               observerAvg$IM2) < 1],
                      NROW(observerAvg)+1))
iHH <- as.numeric(min((1:NROW(observerAvg))[(observerAvg$FR3 +
                                               observerAvg$IM3) < 1],
                      NROW(observerAvg)+1))


###
### NUMBER OF TARGETS
###
minTotalTargetsY <- floor(min(observerSum[,totalTargets]))
maxTotalTargetsY <- ceiling(max(observerSum[,totalTargets]))

png(file=paste(imagePath,"/numberTargets.png", sep=""), width=800, height=600)
qplot(cycle, totalTargets, data=observerSum, geom = "line",
      ylim = c(minTotalTargetsY, maxTotalTargetsY),
      main="Number of Targets", xlab="", ylab="Number")
dev.off()


###
### NUMBER OF TARGETS MINIMUM EQUAL 0
###
minTotalTargetsY <- 0
maxTotalTargetsY <- ceiling(max(observerSum[,totalTargets]))

png(file=paste(imagePath,"/numberTargetsMin0.png", sep=""), width=800, height=600)
qplot(cycle, totalTargets, data=observerSum, geom = "line",
      ylim = c(minTotalTargetsY, maxTotalTargetsY),
      main="Number of Targets", xlab="", ylab="Number")
dev.off()


###
### TARGETS ACCUMULATED WEALTH
####
minWealthY <- floor(min(targetSum[,wealth]))
maxWealthY <- ceiling(max(targetSum[,wealth]))

png(file=paste(imagePath,"/accumulatedTargetsWealth.png", sep=""), width=800, height=600)
qplot(cycle, wealth, data=targetSum, geom = "line",
      ylim = c(minWealthY,maxWealthY),
      main="Targets Accumulated Wealth", xlab="", ylab="Wealth")
dev.off()


###
### ALL TARGETS AVERAGE INCOME
###
minIncomeY <- floor(min(targetAvg[,income]))
maxIncomeY <- ceiling(max(targetAvg[,income]))

png(file=paste(imagePath,"/averageAllTargetsIncome.png", sep=""), width=800, height=600)
qplot(cycle, income, data=targetAvg, geom = "line",
      ylim = c(minIncomeY, maxIncomeY),
      main="All Targets Average Income", xlab="", ylab="Wealth")
dev.off()


###
### AVERAGE PERCENTAGE OF TARGETS INCOME PAID ON EXTORTION
###
minIncomeY <- floor(min(targetAvg[,totalPaid] / 
                          targetAvg[,income+totalPaid+totalPunishment]))
maxIncomeY <- ceiling(max(targetAvg[,totalPaid] /
                            targetAvg[,income+totalPaid+totalPunishment]))

png(file=paste(imagePath,"/averageAllTargetsExtortedIncome.png", sep=""),
    width=800, height=600)
qplot(cycle, totalPaid/(income+totalPaid+totalPunishment), data=targetAvg, geom = "line",
      ylim = c(minIncomeY,maxIncomeY),
      main="Average Targets Income Percentage Paid on Extortion",
      xlab="", ylab="Percentage")
dev.off()


###
### NUMBER OF TOTAL EXTORTERS
###
minTotalExtortersY <- 0
maxTotalExtortersY <- ceiling(max(observerAvg[,totalExtortersFree+totalExtortersImprisoned]))

oX <- as.numeric(min((1:NROW(observerAvg))[(observerAvg$totalExtortersFree +
                                              observerAvg$totalExtortersImprisoned) < 1],
                     NROW(observerAvg)+1))

o <- observerAvg
if(oX <= NROW(observerAvg)){
  o <- observerAvg[oX:NROW(observerAvg), totalExtortersFree := 0]
  o <- observerAvg[oX:NROW(observerAvg), totalExtortersImprisoned := 0]
}

png(file=paste(imagePath,"/numberExtortersTotal.png", sep=""), width=800, height=600)
qplot(cycle, totalExtortersFree+totalExtortersImprisoned, data=o, geom = "line",
      ylim = c(minTotalExtortersY,maxTotalExtortersY),
      main="Total Number of Extorters", xlab="", ylab="Number")
dev.off()


###
### NUMBER OF EXTORTERS PER TARGET BEING EXTORTED
###
minExtortersPerTargetY <- 0
maxExtortersPerTargetY <- ceiling(max(observerSum[,NE0/TE0], na.rm = TRUE))

png(file=paste(imagePath,"/numberExtortersPerTarget.png", sep=""), width=800, height=600)
qplot(cycle, NE0/TE0, data=observerSum, geom = "line",
      ylim = c(minExtortersPerTargetY,maxExtortersPerTargetY),
      main="Number of Extorters Per Extorted Target", xlab="", ylab="Number")
dev.off()


###
### NUMBER OF EXTORTERS BY TYPE
###
minExtortersY <- 0
maxExtortersY <- ceiling(max(max(observerAvg[,FR0+IM0],observerAvg[,FR3+IM3]),
                             max(observerAvg[,FR1+IM1],observerAvg[,FR2+IM2])))

oLL <- observerAvg[FR0+IM0 < 1, FR0 := 0]
oLH <- observerAvg[FR1+IM1 < 1, FR1 := 0]
oHL <- observerAvg[FR2+IM2 < 1, FR2 := 0]
oHH <- observerAvg[FR3+IM3 < 1, FR3 := 0]

plotLL <- qplot(cycle, FR0+IM0, data=observerAvg, geom = "line",
                ylim = c(minExtortersY,maxExtortersY),
                main="LL", xlab="", ylab="Number") +
  geom_vline(xintercept=iLL, linetype="dashed", color="red")
plotLH <- qplot(cycle, FR1+IM1, data=observerAvg, geom = "line",
                ylim = c(minExtortersY,maxExtortersY),
                main="LH", xlab="", ylab="Number") +
  geom_vline(xintercept=iLH, linetype="dashed", color="red")
plotHL <- qplot(cycle, FR2+IM2, data=observerAvg, geom = "line",
                ylim = c(minExtortersY,maxExtortersY),
                main="HL", xlab="", ylab="Number") +
  geom_vline(xintercept=iHL, linetype="dashed", color="red")
plotHH <- qplot(cycle, FR3+IM3, data=observerAvg, geom = "line",
                ylim = c(minExtortersY,maxExtortersY),
                main="HH", xlab="", ylab="Number") +
  geom_vline(xintercept=iHH, linetype="dashed", color="red")
png(file=paste(imagePath,"/numberExtortersType.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Number of Extorters")
dev.off()


###
### EXTORTER ACCUMULATED WEALTH
###
eLL <- extorterSum[which(type == 0),]
eLH <- extorterSum[which(type == 1),]
eHL <- extorterSum[which(type == 2),]
eHH <- extorterSum[which(type == 3),]

if(iLL <= NROW(eLL)){
  eLL <- eLL[iLL:NROW(eLL), wealth := 0]
}
if(iLH <= NROW(eLH)){
  eLH <- eLH[iLH:NROW(eLH), wealth := 0]
}
if(iHL <= NROW(eHL)){
  eHL <- eHL[iHL:NROW(eHL), wealth := 0]
}
if(iHH <= NROW(eHH)){
  eHH <- eHH[iHH:NROW(eHH), wealth := 0]
}

minWealthY <- floor(min(min(eLL[,wealth],eHH[,wealth]),
                        min(eLH[,wealth],eHL[,wealth])))
maxWealthY <- ceiling(max(max(eLL[,wealth],eHH[,wealth]),
                          max(eLH[,wealth],eHL[,wealth])))

plotLL <- qplot(cycle, wealth, data=eLL, geom = "line",
                ylim = c(minWealthY,maxWealthY),
                main="LL", xlab="", ylab="Wealth") +
  geom_vline(xintercept=iLL, linetype="dashed", color="red")
plotLH <- qplot(cycle, wealth, data=eLH, geom = "line",
                ylim = c(minWealthY,maxWealthY),
                main="LH", xlab="", ylab="Wealth") +
  geom_vline(xintercept=iLH, linetype="dashed", color="red")
plotHL <- qplot(cycle, wealth, data=eHL, geom = "line",
                ylim = c(minWealthY,maxWealthY),
                main="HL", xlab="", ylab="Wealth") +
  geom_vline(xintercept=iHL, linetype="dashed", color="red")
plotHH <- qplot(cycle, wealth, data=eHH, geom = "line",
                ylim = c(minWealthY,maxWealthY),
                main="HH", xlab="", ylab="Wealth") +
  geom_vline(xintercept=iHH, linetype="dashed", color="red")
png(file=paste(imagePath,"/accumulatedExtortersWealth.png", sep=""),
    width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Extorters Accumulated Wealth")
dev.off()


###
### ALL EXTORTERS AVERAGE WEALTH
###
eLL <- extorterAvg[which(type == 0),]
eLH <- extorterAvg[which(type == 1),]
eHL <- extorterAvg[which(type == 2),]
eHH <- extorterAvg[which(type == 3),]

if(iLL <= NROW(eLL)){
  eLL <- eLL[iLL:NROW(eLL), wealth := 0]
}
if(iLH <= NROW(eLH)){
  eLH <- eLH[iLH:NROW(eLH), wealth := 0]
}
if(iHL <= NROW(eHL)){
  eHL <- eHL[iHL:NROW(eHL), wealth := 0]
}
if(iHH <= NROW(eHH)){
  eHH <- eHH[iHH:NROW(eHH), wealth := 0]
}

minWealthY <- floor(min(min(eLL[, wealth],eHH[, wealth]),
                        min(eLH[, wealth],eHL[, wealth])))
maxWealthY <- ceiling(max(max(eLL[, wealth],eHH[, wealth]),
                          max(eLH[, wealth],eHL[, wealth])))

plotLL <- qplot(cycle, wealth, data=eLL, geom = "line",
                ylim = c(minWealthY,maxWealthY),
                main="LL", xlab="", ylab="Wealth") +
  geom_vline(xintercept=iLL, linetype="dashed", color="red")
plotLH <- qplot(cycle, wealth, data=eLH, geom = "line",
                ylim = c(minWealthY,maxWealthY),
                main="LH", xlab="", ylab="Wealth") +
  geom_vline(xintercept=iLH, linetype="dashed", color="red")
plotHL <- qplot(cycle, wealth, data=eHL, geom = "line",
                ylim = c(minWealthY,maxWealthY),
                main="HL", xlab="", ylab="Wealth") +
  geom_vline(xintercept=iHL, linetype="dashed", color="red")
plotHH <- qplot(cycle, wealth, data=eHH, geom = "line",
                ylim = c(minWealthY,maxWealthY),
                main="HH", xlab="", ylab="Wealth") +
  geom_vline(xintercept=iHH, linetype="dashed", color="red")
png(file=paste(imagePath,"/averageAllExtortersWealth.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Extorters Average Wealth")
dev.off()


###
### NUMBER OF EXTORTIONS
###
eLL <- extorterSum[which(type == 0), ]
eLH <- extorterSum[which(type == 1), ]
eHL <- extorterSum[which(type == 2), ]
eHH <- extorterSum[which(type == 3), ]

if(iLL <= NROW(eLL)){
  eLL <- eLL[iLL:NROW(eLL), numExtortion := 0]
}
if(iLH <= NROW(eLH)){
  eLH <- eLH[iLH:NROW(eLH), numExtortion := 0]
}
if(iHL <= NROW(eHL)){
  eHL <- eHL[iHL:NROW(eHL), numExtortion := 0]
}
if(iHH <= NROW(eHH)){
  eHH <- eHH[iHH:NROW(eHH), numExtortion := 0]
}

minNumExtortionY <- 0
maxNumExtortionY <- ceiling(max(max(eLL[,numExtortion],eHH[,numExtortion]),
                                max(eLH[,numExtortion],eHL[,numExtortion])))

plotLL <- qplot(cycle, numExtortion, data=eLL, geom = "line",
                ylim = c(minNumExtortionY,maxNumExtortionY),
                main="LL", xlab="", ylab="Number") +
  geom_vline(xintercept=iLL, linetype="dashed", color="red")
plotLH <- qplot(cycle, numExtortion, data=eLH, geom = "line",
                ylim = c(minNumExtortionY,maxNumExtortionY),
                main="LH", xlab="", ylab="Number") +
  geom_vline(xintercept=iLH, linetype="dashed", color="red")
plotHL <- qplot(cycle, numExtortion, data=eHL, geom = "line",
                ylim = c(minNumExtortionY,maxNumExtortionY),
                main="HL", xlab="", ylab="Number") +
  geom_vline(xintercept=iHL, linetype="dashed", color="red")
plotHH <- qplot(cycle, numExtortion, data=eHH, geom = "line",
                ylim = c(minNumExtortionY,maxNumExtortionY),
                main="HH", xlab="", ylab="Number") +
  geom_vline(xintercept=iHH, linetype="dashed", color="red")
png(file=paste(imagePath,"/numberExtortions.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Number of Extortions")
dev.off()


###
### NUMBER OF EXTORTIONS RECEIVED
###
eLL <- extorterSum[which(type == 0), ]
eLH <- extorterSum[which(type == 1), ]
eHL <- extorterSum[which(type == 2), ]
eHH <- extorterSum[which(type == 3), ]

if(iLL <= NROW(eLL)){
  eLL <- eLL[iLL:NROW(eLL), numExtortionReceived := 0]
}
if(iLH <= NROW(eLH)){
  eLH <- eLH[iLH:NROW(eLH), numExtortionReceived := 0]
}
if(iHL <= NROW(eHL)){
  eHL <- eHL[iHL:NROW(eHL), numExtortionReceived := 0]
}
if(iHH <= NROW(eHH)){
  eHH <- eHH[iHH:NROW(eHH), numExtortionReceived := 0]
}

minNumExtortionY <- 0
maxNumExtortionY <- ceiling(max(max(eLL[,numExtortionReceived],eHH[,numExtortionReceived]),
                                max(eLH[,numExtortionReceived],eHL[,numExtortionReceived])))

plotLL <- qplot(cycle, numExtortionReceived, data=eLL, geom = "line",
                ylim = c(minNumExtortionY,maxNumExtortionY),
                main="LL", xlab="", ylab="Number") +
  geom_vline(xintercept=iLL, linetype="dashed", color="red")
plotLH <- qplot(cycle, numExtortionReceived, data=eLH, geom = "line",
                ylim = c(minNumExtortionY,maxNumExtortionY),
                main="LH", xlab="", ylab="Number") +
  geom_vline(xintercept=iLH, linetype="dashed", color="red")
plotHL <- qplot(cycle, numExtortionReceived, data=eHL, geom = "line",
                ylim = c(minNumExtortionY,maxNumExtortionY),
                main="HL", xlab="", ylab="Number") +
  geom_vline(xintercept=iHL, linetype="dashed", color="red")
plotHH <- qplot(cycle, numExtortionReceived, data=eHH, geom = "line",
                ylim = c(minNumExtortionY,maxNumExtortionY),
                main="HH", xlab="", ylab="Number") +
  geom_vline(xintercept=iHH, linetype="dashed", color="red")
png(file=paste(imagePath,"/numberExtortionsReceived.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Number of Extortions Received")
dev.off()


###
### NUMBER OF EXTORTIONS RECEIVED
###
eLL <- extorterSum[which(type == 0), ]
eLH <- extorterSum[which(type == 1), ]
eHL <- extorterSum[which(type == 2), ]
eHH <- extorterSum[which(type == 3), ]

if(iLL <= NROW(eLL)){
  eLL <- eLL[iLL:NROW(eLL), numExtortion := 0]
  eLL <- eLL[iLL:NROW(eLL), numExtortionReceived := 0]
}
if(iLH <= NROW(eLH)){
  eLH <- eLH[iLH:NROW(eLH), numExtortion := 0]
  eLH <- eLH[iLH:NROW(eLH), numExtortionReceived := 0]
}
if(iHL <= NROW(eHL)){
  eHL <- eHL[iHL:NROW(eHL), numExtortion := 0]
  eHL <- eHL[iHL:NROW(eHL), numExtortionReceived := 0]
}
if(iHH <= NROW(eHH)){
  eHH <- eHH[iHH:NROW(eHH), numExtortion := 0]
  eHH <- eHH[iHH:NROW(eHH), numExtortionReceived := 0]
}

minNumExtortionY <- 0
maxNumExtortionY <- ceiling(max(max(eLL[,numExtortion-numExtortionReceived],
                                    eHH[,numExtortion-numExtortionReceived]),
                                max(eLH[,numExtortion-numExtortionReceived],
                                    eHL[,numExtortion-numExtortionReceived])))

plotLL <- qplot(cycle, numExtortion-numExtortionReceived, data=eLL, geom = "line",
                ylim = c(minNumExtortionY,maxNumExtortionY),
                main="LL", xlab="", ylab="Number") +
  geom_vline(xintercept=iLL, linetype="dashed", color="red")
plotLH <- qplot(cycle, numExtortion-numExtortionReceived, data=eLH, geom = "line",
                ylim = c(minNumExtortionY,maxNumExtortionY),
                main="LH", xlab="", ylab="Number") +
  geom_vline(xintercept=iLH, linetype="dashed", color="red")
plotHL <- qplot(cycle, numExtortion-numExtortionReceived, data=eHL, geom = "line",
                ylim = c(minNumExtortionY,maxNumExtortionY),
                main="HL", xlab="", ylab="Number") +
  geom_vline(xintercept=iHL, linetype="dashed", color="red")
plotHH <- qplot(cycle, numExtortion-numExtortionReceived, data=eHH, geom = "line",
                ylim = c(minNumExtortionY,maxNumExtortionY),
                main="HH", xlab="", ylab="Number") +
  geom_vline(xintercept=iHH, linetype="dashed", color="red")
png(file=paste(imagePath,"/numberExtortionsNotReceived.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Number of Extortions Not Received")
dev.off()


###
### AVERAGE NUMBER OF TARGETS PER EXTORTER
###
eLL <- extorterAvg[which(type == 0), ]
eLH <- extorterAvg[which(type == 1), ]
eHL <- extorterAvg[which(type == 2), ]
eHH <- extorterAvg[which(type == 3), ]

if(iLL <= NROW(eLL)){
  eLL <- eLL[iLL:NROW(eLL), numTargets := 0]
}
if(iLH <= NROW(eLH)){
  eLH <- eLH[iLH:NROW(eLH), numTargets := 0]
}
if(iHL <= NROW(eHL)){
  eHL <- eHL[iHL:NROW(eHL), numTargets := 0]
}
if(iHH <= NROW(eHH)){
  eHH <- eHH[iHH:NROW(eHH), numTargets := 0]
}

minNumTargetsY <- 0
maxNumTargetsY <- ceiling(max(max(eLL[,numTargets],eHH[,numTargets]),
                              max(eLH[,numTargets],eHL[,numTargets])))

plotLL <- qplot(cycle, numTargets, data=eLL, geom = "line",
                ylim = c(minNumTargetsY,maxNumTargetsY),
                main="LL", xlab="", ylab="Number") +
  geom_vline(xintercept=iLL, linetype="dashed", color="red")
plotLH <- qplot(cycle, numTargets, data=eLH, geom = "line",
                ylim = c(minNumTargetsY,maxNumTargetsY),
                main="LH", xlab="", ylab="Number") +
  geom_vline(xintercept=iLH, linetype="dashed", color="red")
plotHL <- qplot(cycle, numTargets, data=eHL, geom = "line",
                ylim = c(minNumTargetsY,maxNumTargetsY),
                main="HL", xlab="", ylab="Number") +
  geom_vline(xintercept=iHL, linetype="dashed", color="red")
plotHH <- qplot(cycle, numTargets, data=eHH, geom = "line",
                ylim = c(minNumTargetsY,maxNumTargetsY),
                main="HH", xlab="", ylab="Number") +
  geom_vline(xintercept=iHH, linetype="dashed", color="red")
png(file=paste(imagePath,"/numberTargetsPerExtorter.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Number of Targets per Extorter")
dev.off()


###
### NUMBER OF ATTACK RETALIATION
###
eLL <- extorterSum[which(type == 0), ]
eLH <- extorterSum[which(type == 1), ]
eHL <- extorterSum[which(type == 2), ]
eHH <- extorterSum[which(type == 3), ]

if(iLL <= NROW(eLL)){
  eLL <- eLL[iLL:NROW(eLL), numAttackRetaliation := 0]
}
if(iLH <= NROW(eLH)){
  eLH <- eLH[iLH:NROW(eLH), numAttackRetaliation := 0]
}
if(iHL <= NROW(eHL)){
  eHL <- eHL[iHL:NROW(eHL), numAttackRetaliation := 0]
}
if(iHH <= NROW(eHH)){
  eHH <- eHH[iHH:NROW(eHH), numAttackRetaliation := 0]
}

minNumRetaliationY <- 0
maxNumRetaliationY <- ceiling(max(max(eLL[,numAttackRetaliation],eHH[,numAttackRetaliation]),
                                  max(eLH[,numAttackRetaliation],eHL[,numAttackRetaliation])))

plotLL <- qplot(cycle, numAttackRetaliation, data=eLL, geom = "line",
                ylim = c(minNumRetaliationY,maxNumRetaliationY),
                main="LL", xlab="", ylab="Number") +
  geom_vline(xintercept=iLL, linetype="dashed", color="red")
plotLH <- qplot(cycle, numAttackRetaliation, data=eLH, geom = "line",
                ylim = c(minNumRetaliationY,maxNumRetaliationY),
                main="LH", xlab="", ylab="Number") +
  geom_vline(xintercept=iLH, linetype="dashed", color="red")
plotHL <- qplot(cycle, numAttackRetaliation, data=eHL, geom = "line",
                ylim = c(minNumRetaliationY,maxNumRetaliationY),
                main="HL", xlab="", ylab="Number") +
  geom_vline(xintercept=iHL, linetype="dashed", color="red")
plotHH <- qplot(cycle, numAttackRetaliation, data=eHH, geom = "line",
                ylim = c(minNumRetaliationY,maxNumRetaliationY),
                main="HH", xlab="", ylab="Number") +
  geom_vline(xintercept=iHH, linetype="dashed", color="red")
png(file=paste(imagePath,"/numberRetaliationAttack.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Number of Attack Retaliation")
dev.off()


###
### NUMBER OF NON ATTACK RETALIATION
###
eLL <- extorterSum[which(type == 0), ]
eLH <- extorterSum[which(type == 1), ]
eHL <- extorterSum[which(type == 2), ]
eHH <- extorterSum[which(type == 3), ]

if(iLL <= NROW(eLL)){
  eLL <- eLL[iLL:NROW(eLL), numNonAttackRetaliation := 0]
}
if(iLH <= NROW(eLH)){
  eLH <- eLH[iLH:NROW(eLH), numNonAttackRetaliation := 0]
}
if(iHL <= NROW(eHL)){
  eHL <- eHL[iHL:NROW(eHL), numNonAttackRetaliation := 0]
}
if(iHH <= NROW(eHH)){
  eHH <- eHH[iHH:NROW(eHH), numNonAttackRetaliation := 0]
}

minNumNonRetaliationY <- 0
maxNumNonRetaliationY <- ceiling(max(max(eLL[,numNonAttackRetaliation],
                                         eHH[,numNonAttackRetaliation]),
                                   max(eLH[,numNonAttackRetaliation],
                                       eHL[,numNonAttackRetaliation])))

plotLL <- qplot(cycle, numNonAttackRetaliation, data=eLL, geom = "line",
                ylim = c(minNumNonRetaliationY,maxNumNonRetaliationY),
                main="LL", xlab="", ylab="Number") +
  geom_vline(xintercept=iLL, linetype="dashed", color="red")
plotLH <- qplot(cycle, numNonAttackRetaliation, data=eLH, geom = "line",
                ylim = c(minNumNonRetaliationY,maxNumNonRetaliationY),
                main="LH", xlab="", ylab="Number") +
  geom_vline(xintercept=iLH, linetype="dashed", color="red")
plotHL <- qplot(cycle, numNonAttackRetaliation, data=eHL, geom = "line",
                ylim = c(minNumNonRetaliationY,maxNumNonRetaliationY),
                main="HL", xlab="", ylab="Number") +
  geom_vline(xintercept=iHL, linetype="dashed", color="red")
plotHH <- qplot(cycle, numNonAttackRetaliation, data=eHH, geom = "line",
                ylim = c(minNumNonRetaliationY,maxNumNonRetaliationY),
                main="HH", xlab="", ylab="Number") +
  geom_vline(xintercept=iHH, linetype="dashed", color="red")
png(file=paste(imagePath,"/numberRetaliationNonAttack.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Number of Non Attack Retaliation")
dev.off()


###
### NUMBER OF RECEIVED RETALIATION
###
eLL <- extorterSum[which(type == 0), ]
eLH <- extorterSum[which(type == 1), ]
eHL <- extorterSum[which(type == 2), ]
eHH <- extorterSum[which(type == 3), ]

if(iLL <= NROW(eLL)){
  eLL <- eLL[iLL:NROW(eLL), numReceivedAttackRetaliation := 0]
}
if(iLH <= NROW(eLH)){
  eLH <- eLH[iLH:NROW(eLH), numReceivedAttackRetaliation := 0]
}
if(iHL <= NROW(eHL)){
  eHL <- eHL[iHL:NROW(eHL), numReceivedAttackRetaliation := 0]
}
if(iHH <= NROW(eHH)){
  eHH <- eHH[iHH:NROW(eHH), numReceivedAttackRetaliation := 0]
}

minNumRetaliationY <- 0
maxNumRetaliationY <- ceiling(max(max(eLL[,numReceivedAttackRetaliation],
                                      eHH[,numReceivedAttackRetaliation]),
                                  max(eLH[,numReceivedAttackRetaliation],
                                      eHL[,numReceivedAttackRetaliation])))

plotLL <- qplot(cycle, numReceivedAttackRetaliation, data=eLL, geom = "line",
                ylim = c(minNumRetaliationY,maxNumRetaliationY),
                main="LL", xlab="", ylab="Number") +
  geom_vline(xintercept=iLL, linetype="dashed", color="red")
plotLH <- qplot(cycle, numReceivedAttackRetaliation, data=eLH, geom = "line",
                ylim = c(minNumRetaliationY,maxNumRetaliationY),
                main="LH", xlab="", ylab="Number") +
  geom_vline(xintercept=iLH, linetype="dashed", color="red")
plotHL <- qplot(cycle, numReceivedAttackRetaliation, data=eHL, geom = "line",
                ylim = c(minNumRetaliationY,maxNumRetaliationY),
                main="HL", xlab="", ylab="Number") +
  geom_vline(xintercept=iHL, linetype="dashed", color="red")
plotHH <- qplot(cycle, numReceivedAttackRetaliation, data=eHH, geom = "line",
                ylim = c(minNumRetaliationY,maxNumRetaliationY),
                main="HH", xlab="", ylab="Number") +
  geom_vline(xintercept=iHH, linetype="dashed", color="red")
png(file=paste(imagePath,"/numberReceivedRetaliationAttack.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Number of Received Attack Retaliation")
dev.off()


###
### NUMBER OF COUNTERATTACK RETALIATION
###
eLL <- extorterSum[which(type == 0), ]
eLH <- extorterSum[which(type == 1), ]
eHL <- extorterSum[which(type == 2), ]
eHH <- extorterSum[which(type == 3), ]

if(iLL <= NROW(eLL)){
  eLL <- eLL[iLL:NROW(eLL), numCounterattackRetaliation := 0]
}
if(iLH <= NROW(eLH)){
  eLH <- eLH[iLH:NROW(eLH), numCounterattackRetaliation := 0]
}
if(iHL <= NROW(eHL)){
  eHL <- eHL[iHL:NROW(eHL), numCounterattackRetaliation := 0]
}
if(iHH <= NROW(eHH)){
  eHH <- eHH[iHH:NROW(eHH), numCounterattackRetaliation := 0]
}

minNumRetaliationY <- 0
maxNumRetaliationY <- ceiling(max(max(eLL[,numCounterattackRetaliation],
                                      eHH[,numCounterattackRetaliation]),
                                  max(eLH[,numCounterattackRetaliation],
                                      eHL[,numCounterattackRetaliation])))

plotLL <- qplot(cycle, numCounterattackRetaliation, data=eLL, geom = "line",
                ylim = c(minNumRetaliationY,maxNumRetaliationY),
                main="LL", xlab="", ylab="Number") +
  geom_vline(xintercept=iLL, linetype="dashed", color="red")
plotLH <- qplot(cycle, numCounterattackRetaliation, data=eLH, geom = "line",
                ylim = c(minNumRetaliationY,maxNumRetaliationY),
                main="LH", xlab="", ylab="Number") +
  geom_vline(xintercept=iLH, linetype="dashed", color="red")
plotHL <- qplot(cycle, numCounterattackRetaliation, data=eHL, geom = "line",
                ylim = c(minNumRetaliationY,maxNumRetaliationY),
                main="HL", xlab="", ylab="Number") +
  geom_vline(xintercept=iHL, linetype="dashed", color="red")
plotHH <- qplot(cycle, numCounterattackRetaliation, data=eHH, geom = "line",
                ylim = c(minNumRetaliationY,maxNumRetaliationY),
                main="HH", xlab="", ylab="Number") +
  geom_vline(xintercept=iHH, linetype="dashed", color="red")
png(file=paste(imagePath,"/numberCounterretaliationAttack.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Number of Counterattack Retaliation")
dev.off()


###
### NUMBER OF RECEIVED COUNTERATTACK RETALIATION
###
eLL <- extorterSum[which(type == 0), ]
eLH <- extorterSum[which(type == 1), ]
eHL <- extorterSum[which(type == 2), ]
eHH <- extorterSum[which(type == 3), ]

if(iLL <= NROW(eLL)){
  eLL <- eLL[iLL:NROW(eLL), numReceivedCounterattackRetaliation := 0]
}
if(iLH <= NROW(eLH)){
  eLH <- eLH[iLH:NROW(eLH), numReceivedCounterattackRetaliation := 0]
}
if(iHL <= NROW(eHL)){
  eHL <- eHL[iHL:NROW(eHL), numReceivedCounterattackRetaliation := 0]
}
if(iHH <= NROW(eHH)){
  eHH <- eHH[iHH:NROW(eHH), numReceivedCounterattackRetaliation := 0]
}

minNumRetaliationY <- 0
maxNumRetaliationY <- ceiling(max(max(eLL[,numReceivedCounterattackRetaliation],
                                      eHH[,numReceivedCounterattackRetaliation]),
                                  max(eLH[,numReceivedCounterattackRetaliation],
                                      eHL[,numReceivedCounterattackRetaliation])))

plotLL <- qplot(cycle, numReceivedCounterattackRetaliation, data=eLL, geom = "line",
                ylim = c(minNumRetaliationY,maxNumRetaliationY),
                main="LL", xlab="", ylab="Number") +
  geom_vline(xintercept=iLL, linetype="dashed", color="red")
plotLH <- qplot(cycle, numReceivedCounterattackRetaliation, data=eLH, geom = "line",
                ylim = c(minNumRetaliationY,maxNumRetaliationY),
                main="LH", xlab="", ylab="Number") +
  geom_vline(xintercept=iLH, linetype="dashed", color="red")
plotHL <- qplot(cycle, numReceivedCounterattackRetaliation, data=eHL, geom = "line",
                ylim = c(minNumRetaliationY,maxNumRetaliationY),
                main="HL", xlab="", ylab="Number") +
  geom_vline(xintercept=iHL, linetype="dashed", color="red")
plotHH <- qplot(cycle, numReceivedCounterattackRetaliation, data=eHH, geom = "line",
                ylim = c(minNumRetaliationY,maxNumRetaliationY),
                main="HH", xlab="", ylab="Number") +
  geom_vline(xintercept=iHH, linetype="dashed", color="red")
png(file=paste(imagePath,"/numberReceivedCounterretaliationAttack.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Number of Received Counterattack Retaliation")
dev.off()


###
### EXTORTER RUNAWAY PROTECTION
###
eLL <- extorterSum[which(type == 0), ]
eLH <- extorterSum[which(type == 1), ]
eHL <- extorterSum[which(type == 2), ]
eHH <- extorterSum[which(type == 3), ]

if(iLL <= NROW(eLL)){
  eLL <- eLL[iLL:NROW(eLL), numRunawayProtection := 0]
  eLL <- eLL[iLL:NROW(eLL), numAttackProtection := 1]
  eLL <- eLL[iLL:NROW(eLL), numReceivedAttackProtection := 1]
  eLL <- eLL[iLL:NROW(eLL), numCounterattackProtection := 1]
  eLL <- eLL[iLL:NROW(eLL), numReceivedCounterattackProtection := 1]
}
if(iLH <= NROW(eLH)){
  eLH <- eLH[iLH:NROW(eLH), numRunawayProtection := 0]
  eLH <- eLH[iLH:NROW(eLH), numAttackProtection := 1]
  eLH <- eLH[iLH:NROW(eLH), numReceivedAttackProtection := 1]
  eLH <- eLH[iLH:NROW(eLH), numCounterattackProtection := 1]
  eLH <- eLH[iLH:NROW(eLH), numReceivedCounterattackProtection := 1]
}
if(iHL <= NROW(eHL)){
  eHL <- eHL[iHL:NROW(eHL), numRunawayProtection := 0]
  eHL <- eHL[iHL:NROW(eHL), numAttackProtection := 1]
  eHL <- eHL[iHL:NROW(eHL), numReceivedAttackProtection := 1]
  eHL <- eHL[iHL:NROW(eHL), numCounterattackProtection := 1]
  eHL <- eHL[iHL:NROW(eHL), numReceivedCounterattackProtection := 1]
}
if(iHH <= NROW(eHH)){
  eHH <- eHH[iHH:NROW(eHH), numRunawayProtection := 0]
  eHH <- eHH[iHH:NROW(eHH), numAttackProtection := 1]
  eHH <- eHH[iHH:NROW(eHH), numReceivedAttackProtection := 1]
  eHH <- eHH[iHH:NROW(eHH), numCounterattackProtection := 1]
  eHH <- eHH[iHH:NROW(eHH), numReceivedCounterattackProtection := 1]
}

minRunawayY <- floor(min(min(eLL[,numRunawayProtection],
                             eHH[,numRunawayProtection]),
                         min(eLH[,numRunawayProtection],
                             eHL[,numRunawayProtection])))
maxRunawayY <- ceiling(max(max(eLL[,numRunawayProtection],
                               eHH[,numRunawayProtection]),
                           max(eLH[,numRunawayProtection],
                               eHL[,numRunawayProtection])))

plotLL <- qplot(cycle, numRunawayProtection,
                data=eLL, geom = "line",
                ylim = c(minRunawayY,maxRunawayY),
                main="LL", xlab="", ylab="Number of Runaway Protection") +
  geom_vline(xintercept=iLL, linetype="dashed", color="red")
plotLH <- qplot(cycle, numRunawayProtection,
                data=eLH, geom = "line",
                ylim = c(minRunawayY,maxRunawayY),
                main="LH", xlab="", ylab="Number of Runaway Protection") +
  geom_vline(xintercept=iLH, linetype="dashed", color="red")
plotHL <- qplot(cycle, numRunawayProtection,
                data=eHL, geom = "line",
                ylim = c(minRunawayY,maxRunawayY),
                main="HL", xlab="", ylab="Number of Runaway Protection") +
  geom_vline(xintercept=iHL, linetype="dashed", color="red")
plotHH <- qplot(cycle, numRunawayProtection,
                data=eHH, geom = "line",
                ylim = c(minRunawayY,maxRunawayY),
                main="HH", xlab="", ylab="Number of Runaway Protection") +
  geom_vline(xintercept=iHH, linetype="dashed", color="red")
png(file=paste(imagePath,"/numberRunawayProtection.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Extorters Number of Runaway Protection")
dev.off()


###
### EXTORTER RUNAWAY RETALIATION
###
eLL <- extorterSum[which(type == 0), ]
eLH <- extorterSum[which(type == 1), ]
eHL <- extorterSum[which(type == 2), ]
eHH <- extorterSum[which(type == 3), ]

if(iLL <= NROW(eLL)){
  eLL <- eLL[iLL:NROW(eLL), numRunawayRetaliation := 0]
  eLL <- eLL[iLL:NROW(eLL), numAttackRetaliation := 1]
  eLL <- eLL[iLL:NROW(eLL), numReceivedAttackRetaliation := 1]
  eLL <- eLL[iLL:NROW(eLL), numCounterattackRetaliation := 1]
  eLL <- eLL[iLL:NROW(eLL), numReceivedCounterattackRetaliation := 1]
}
if(iLH <= NROW(eLH)){
  eLH <- eLH[iLH:NROW(eLH), numRunawayRetaliation := 0]
  eLH <- eLH[iLH:NROW(eLH), numAttackRetaliation := 1]
  eLH <- eLH[iLH:NROW(eLH), numReceivedAttackRetaliation := 1]
  eLH <- eLH[iLH:NROW(eLH), numCounterattackRetaliation := 1]
  eLH <- eLH[iLH:NROW(eLH), numReceivedCounterattackRetaliation := 1]
}
if(iHL <= NROW(eHL)){
  eHL <- eHL[iHL:NROW(eHL), numRunawayRetaliation := 0]
  eHL <- eHL[iHL:NROW(eHL), numAttackRetaliation := 1]
  eHL <- eHL[iHL:NROW(eHL), numReceivedAttackRetaliation := 1]
  eHL <- eHL[iHL:NROW(eHL), numCounterattackRetaliation := 1]
  eHL <- eHL[iHL:NROW(eHL), numReceivedCounterattackRetaliation := 1]
}
if(iHH <= NROW(eHH)){
  eHH <- eHH[iHH:NROW(eHH), numRunawayRetaliation := 0]
  eHH <- eHH[iHH:NROW(eHH), numAttackRetaliation := 1]
  eHH <- eHH[iHH:NROW(eHH), numReceivedAttackRetaliation := 1]
  eHH <- eHH[iHH:NROW(eHH), numCounterattackRetaliation := 1]
  eHH <- eHH[iHH:NROW(eHH), numReceivedCounterattackRetaliation := 1]
}

minRunawayY <- floor(min(min(eLL[,numRunawayRetaliation],
                             eHH[,numRunawayRetaliation]),
                         min(eLH[,numRunawayRetaliation],
                             eHL[,numRunawayRetaliation])))
maxRunawayY <- ceiling(max(max(eLL[,numRunawayRetaliation],
                               eHH[,numRunawayRetaliation]),
                           max(eLH[,numRunawayRetaliation],
                               eHL[,numRunawayRetaliation])))

plotLL <- qplot(cycle, numRunawayRetaliation,
                data=eLL, geom = "line",
                ylim = c(minRunawayY,maxRunawayY),
                main="LL", xlab="", ylab="Number of Runaway Retaliation") +
  geom_vline(xintercept=iLL, linetype="dashed", color="red")
plotLH <- qplot(cycle, numRunawayRetaliation,
                data=eLH, geom = "line",
                ylim = c(minRunawayY,maxRunawayY),
                main="LH", xlab="", ylab="Number of Runaway Retaliation") +
  geom_vline(xintercept=iLH, linetype="dashed", color="red")
plotHL <- qplot(cycle, numRunawayRetaliation,
                data=eHL, geom = "line",
                ylim = c(minRunawayY,maxRunawayY),
                main="HL", xlab="", ylab="Number of Runaway Retaliation") +
  geom_vline(xintercept=iHL, linetype="dashed", color="red")
plotHH <- qplot(cycle, numRunawayRetaliation,
                data=eHH, geom = "line",
                ylim = c(minRunawayY,maxRunawayY),
                main="HH", xlab="", ylab="Number of Runaway Retaliation") +
  geom_vline(xintercept=iHH, linetype="dashed", color="red")
png(file=paste(imagePath,"/numberRunawayRetaliation.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Extorters Number of Runaway Retaliation")
dev.off()


###
### EXTORTER VIOLENCE
###
eLL <- extorterSum[which(type == 0), ]
eLH <- extorterSum[which(type == 1), ]
eHL <- extorterSum[which(type == 2), ]
eHH <- extorterSum[which(type == 3), ]

if(iLL <= NROW(eLL)){
  eLL <- eLL[iLL:NROW(eLL), numCounterattackProtection := 0]
  eLL <- eLL[iLL:NROW(eLL), numCounterattackRetaliation := 0]
  eLL <- eLL[iLL:NROW(eLL), numPunishment := 0]
}
if(iLH <= NROW(eLH)){
  eLH <- eLH[iLH:NROW(eLH), numCounterattackProtection := 0]
  eLH <- eLH[iLH:NROW(eLH), numCounterattackRetaliation := 0]
  eLH <- eLH[iLH:NROW(eLH), numPunishment := 0]
}
if(iHL <= NROW(eHL)){
  eHL <- eHL[iHL:NROW(eHL), numCounterattackProtection := 0]
  eHL <- eHL[iHL:NROW(eHL), numCounterattackRetaliation := 0]
  eHL <- eHL[iHL:NROW(eHL), numPunishment := 0]
}
if(iHH <= NROW(eHH)){
  eHH <- eHH[iHH:NROW(eHH), numCounterattackProtection := 0]
  eHH <- eHH[iHH:NROW(eHH), numCounterattackRetaliation := 0]
  eHH <- eHH[iHH:NROW(eHH), numPunishment := 0]
}

minViolenceY <- floor(min(min(eLL[,numCounterattackProtection+
                                    numCounterattackRetaliation+
                                    numPunishment],
                              eHH[,numCounterattackProtection+
                                    numCounterattackRetaliation+
                                    numPunishment]),
                          min(eLH[,numCounterattackProtection+
                                    numCounterattackRetaliation+
                                    numPunishment],
                              eHL[,numCounterattackProtection+
                                    numCounterattackRetaliation+
                                    numPunishment])))
maxViolenceY <- ceiling(max(max(eLL[,numCounterattackProtection+
                                      numCounterattackRetaliation+
                                      numPunishment],
                                eHH[,numCounterattackProtection+
                                      numCounterattackRetaliation+
                                      numPunishment]),
                            max(eLH[,numCounterattackProtection+
                                      numCounterattackRetaliation+
                                      numPunishment],
                                eHL[,numCounterattackProtection+
                                      numCounterattackRetaliation+
                                      numPunishment])))

plotLL <- qplot(cycle, numCounterattackProtection+numCounterattackRetaliation+numPunishment,
                data=eLL, geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="LL", xlab="", ylab="Number of Fights + Punishments") +
  geom_vline(xintercept=iLL, linetype="dashed", color="red")
plotLH <- qplot(cycle, numCounterattackProtection+numCounterattackRetaliation+numPunishment, data=eLH, geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="LH", xlab="", ylab="Number of Fights + Punishments") +
  geom_vline(xintercept=iLH, linetype="dashed", color="red")
plotHL <- qplot(cycle, numCounterattackProtection+numCounterattackRetaliation+numPunishment, data=eHL, geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="HL", xlab="", ylab="Number of Fights + Punishments") +
  geom_vline(xintercept=iHL, linetype="dashed", color="red")
plotHH <- qplot(cycle, numCounterattackProtection+numCounterattackRetaliation+numPunishment, data=eHH, geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="HH", xlab="", ylab="Number of Fights + Punishments") +
  geom_vline(xintercept=iHH, linetype="dashed", color="red")
png(file=paste(imagePath,"/numberViolence.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Extorters Number of Violent Activities")
dev.off()


###
### EXTORTER VIOLENCE HIGHLIGHT 1:100
###
minViolenceY <- 0
maxViolenceY <- ceiling(max(max(eLL[1:100,numCounterattackProtection+
                                      numCounterattackRetaliation+
                                      numPunishment],
                                eHH[1:100,numCounterattackProtection+
                                      numCounterattackRetaliation+
                                      numPunishment]),
                            max(eLH[1:100,numCounterattackProtection+
                                      numCounterattackRetaliation+
                                      numPunishment],
                                eHL[1:100,numCounterattackProtection+
                                      numCounterattackRetaliation+
                                      numPunishment])))

plotLL <- qplot(cycle, numCounterattackProtection+numCounterattackRetaliation+numPunishment,
                data=eLL[1:100], geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="LL", xlab="", ylab="Number of Fights + Punishments")
plotLH <- qplot(cycle, numCounterattackProtection+numCounterattackRetaliation+numPunishment,
                data=eLH[1:100], geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="LH", xlab="", ylab="Number of Fights + Punishments")
plotHL <- qplot(cycle, numCounterattackProtection+numCounterattackRetaliation+numPunishment,
                data=eHL[1:100], geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="HL", xlab="", ylab="Number of Fights + Punishments")
plotHH <- qplot(cycle, numCounterattackProtection+numCounterattackRetaliation+numPunishment,
                data=eHH[1:100], geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="HH", xlab="", ylab="Number of Fights + Punishments")
png(file=paste(imagePath,"/numberViolenceHighlighted:1-100.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Number of Violent Activities [100]")
dev.off()


###
### EXTORTER VIOLENCE HIGHLIGHT 100:END
###
minViolenceY <- 0
maxViolenceY <- ceiling(max(max(eLL[100:NROW(eLL),numCounterattackProtection+
                                      numCounterattackRetaliation+
                                      numPunishment],
                                eHH[100:NROW(eHH),numCounterattackProtection+
                                      numCounterattackRetaliation+
                                      numPunishment]),
                            max(eLH[100:NROW(eLH),numCounterattackProtection+
                                      numCounterattackRetaliation+
                                      numPunishment],
                                eHL[100:NROW(eHL),numCounterattackProtection+
                                      numCounterattackRetaliation+
                                      numPunishment])))

plotLL <- qplot(cycle, numCounterattackProtection+numCounterattackRetaliation+numPunishment,
                data=eLL[100:NROW(eLL)], geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="LL", xlab="", ylab="Number of Fights + Punishments")
plotLH <- qplot(cycle, numCounterattackProtection+numCounterattackRetaliation+numPunishment,
                data=eLH[100:NROW(eLH)], geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="LH", xlab="", ylab="Number of Fights + Punishments")
plotHL <- qplot(cycle, numCounterattackProtection+numCounterattackRetaliation+numPunishment,
                data=eHL[100:NROW(eHL)], geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="HL", xlab="", ylab="Number of Fights + Punishments")
plotHH <- qplot(cycle, numCounterattackProtection+numCounterattackRetaliation+numPunishment,
                data=eHH[100:NROW(eHH)], geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="HH", xlab="", ylab="Number of Fights + Punishments")
png(file=paste(imagePath,"/numberViolenceHighlighted:100-END.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Number of Violent Activities [100:END]")
dev.off()


###
### EXTORTER LOSS OF WEALTH ON VIOLENCE
###
eLL <- extorterSum[which(type == 0), ]
eLH <- extorterSum[which(type == 1), ]
eHL <- extorterSum[which(type == 2), ]
eHH <- extorterSum[which(type == 3), ]

if(iLL <= NROW(eLL)){
  eLL <- eLL[iLL:NROW(eLL), totalLostFightProtection := 0]
  eLL <- eLL[iLL:NROW(eLL), totalLostFightRetaliation := 0]
  eLL <- eLL[iLL:NROW(eLL), totalLostPunishment := 0]
}
if(iLH <= NROW(eLH)){
  eLH <- eLH[iLH:NROW(eLH), totalLostFightProtection := 0]
  eLH <- eLH[iLH:NROW(eLH), totalLostFightRetaliation := 0]
  eLH <- eLH[iLH:NROW(eLH), totalLostPunishment := 0]
}
if(iHL <= NROW(eHL)){
  eHL <- eHL[iHL:NROW(eHL), totalLostFightProtection := 0]
  eHL <- eHL[iHL:NROW(eHL), totalLostFightRetaliation := 0]
  eHL <- eHL[iHL:NROW(eHL), totalLostPunishment := 0]
}
if(iHH <= NROW(eHH)){
  eHH <- eHH[iHH:NROW(eHH), totalLostFightProtection := 0]
  eHH <- eHH[iHH:NROW(eHH), totalLostFightRetaliation := 0]
  eHH <- eHH[iHH:NROW(eHH), totalLostPunishment := 0]
}

lastLine <- max(max(nrow(eLL),nrow(eHH)),max(nrow(eLH),nrow(eHL)))

for(index in 2:lastLine){
  
  if(index < nrow(eLL)){
    aux <- eLL[index-1,totalLostFightProtection] +
      eLL[index,totalLostFightProtection]
    eLL[index,totalLostFightProtection := aux]
    
    aux <- eLL[index-1,totalLostFightRetaliation] +
      eLL[index,totalLostFightRetaliation]
    eLL[index,totalLostFightRetaliation := aux]
    
    aux <- eLL[index-1,totalLostPunishment] +
      eLL[index,totalLostPunishment]
    eLL[index,totalLostPunishment := aux]
  }
  
  if(index < nrow(eLH)){
    aux <- eLH[index-1,totalLostFightProtection] +
      eLH[index,totalLostFightProtection]
    eLH[index,totalLostFightProtection := aux]
    
    aux <- eLH[index-1,totalLostFightRetaliation] +
      eLH[index,totalLostFightRetaliation]
    eLH[index,totalLostFightRetaliation := aux]
    
    aux <- eLH[index-1,totalLostPunishment] +
      eLH[index,totalLostPunishment]
    eLH[index,totalLostPunishment := aux]
  }
  
  if(index < nrow(eHL)){
    aux <- eHL[index-1,totalLostFightProtection] +
      eHL[index,totalLostFightProtection]
    eHL[index,totalLostFightProtection := aux]
    
    aux <- eHL[index-1,totalLostFightRetaliation] +
      eHL[index,totalLostFightRetaliation]
    eHL[index,totalLostFightRetaliation := aux]
    
    aux <- eHL[index-1,totalLostPunishment] +
      eHL[index,totalLostPunishment]
    eHL[index,totalLostPunishment := aux]
  }
  
  if(index < nrow(eHH)){
    aux <- eHH[index-1,totalLostFightProtection] +
      eHH[index,totalLostFightProtection]
    eHH[index,totalLostFightProtection := aux]
    
    aux <- eHH[index-1,totalLostFightRetaliation] +
      eHH[index,totalLostFightRetaliation]
    eHH[index,totalLostFightRetaliation := aux]
    
    aux <- eHH[index-1,totalLostPunishment] +
      eHH[index,totalLostPunishment]
    eHH[index,totalLostPunishment := aux]
  }
}

acELL <- eLL
acELH <- eLH
acEHL <- eHL
acEHH <- eHH

minViolenceY <- floor(min(min(eLL[,totalLostFightProtection+
                                    totalLostFightRetaliation+
                                    totalLostPunishment],
                              eHH[,totalLostFightProtection+
                                    totalLostFightRetaliation+
                                    totalLostPunishment]),
                          min(eLH[,totalLostFightProtection+
                                    totalLostFightRetaliation+
                                    totalLostPunishment],
                              eHL[,totalLostFightProtection+
                                    totalLostFightRetaliation+
                                    totalLostPunishment])))
maxViolenceY <- ceiling(max(max(eLL[,totalLostFightProtection+
                                      totalLostFightRetaliation+
                                      totalLostPunishment],
                                eHH[,totalLostFightProtection+
                                      totalLostFightRetaliation+
                                      totalLostPunishment]),
                            max(eLH[,totalLostFightProtection+
                                      totalLostFightRetaliation+
                                      totalLostPunishment],
                                eHL[,totalLostFightProtection+
                                      totalLostFightRetaliation+
                                      totalLostPunishment])))

plotLL <- qplot(cycle, totalLostFightProtection+totalLostFightRetaliation+totalLostPunishment,
                data=eLL, geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="LL", xlab="", ylab="Wealth") +
  geom_vline(xintercept=iLL, linetype="dashed", color="red")
plotLH <- qplot(cycle, totalLostFightProtection+totalLostFightRetaliation+totalLostPunishment,
                data=eLH, geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="LH", xlab="", ylab="Wealth") +
  geom_vline(xintercept=iLH, linetype="dashed", color="red")
plotHL <- qplot(cycle, totalLostFightProtection+totalLostFightRetaliation+totalLostPunishment,
                data=eHL, geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="HL", xlab="", ylab="Wealth") +
  geom_vline(xintercept=iHL, linetype="dashed", color="red")
plotHH <- qplot(cycle, totalLostFightProtection+totalLostFightRetaliation+totalLostPunishment,
                data=eHH, geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="HH", xlab="", ylab="Wealth") +
  geom_vline(xintercept=iHH, linetype="dashed", color="red")
png(file=paste(imagePath,"/lossWealthViolence.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Loss of Wealth on Violence")
dev.off()


###
### EXTORTER LOSS OF WEALTH PER FIGHT
###
eLL <- acELL
eLH <- acELH
eHL <- acEHL
eHH <- acEHH

eLL[which((numCounterattackRetaliation == 0) | (numReceivedCounterattackRetaliation == 0)),
    totalLostFightProtection := 0]
eLL[which((numCounterattackRetaliation == 0) | (numReceivedCounterattackRetaliation == 0)),
    totalLostFightRetaliation := 0]
eLL[which(numCounterattackRetaliation == 0), numCounterattackRetaliation := 1]
eLL[which(numReceivedCounterattackRetaliation == 0), numReceivedCounterattackRetaliation := 1]

eLH[which((numCounterattackRetaliation == 0) | (numReceivedCounterattackRetaliation == 0)),
    totalLostFightProtection := 0]
eLH[which((numCounterattackRetaliation == 0) | (numReceivedCounterattackRetaliation == 0)),
    totalLostFightRetaliation := 0]
eLH[which(numCounterattackRetaliation == 0), numCounterattackRetaliation := 1]
eLH[which(numReceivedCounterattackRetaliation == 0), numReceivedCounterattackRetaliation := 1]

eHL[which((numCounterattackRetaliation == 0) | (numReceivedCounterattackRetaliation == 0)),
    totalLostFightProtection := 0]
eHL[which((numCounterattackRetaliation == 0) | (numReceivedCounterattackRetaliation == 0)),
    totalLostFightRetaliation := 0]
eHL[which(numCounterattackRetaliation == 0), numCounterattackRetaliation := 1]
eHL[which(numReceivedCounterattackRetaliation == 0), numReceivedCounterattackRetaliation := 1]

eHH[which((numCounterattackRetaliation == 0) | (numReceivedCounterattackRetaliation == 0)),
    totalLostFightProtection := 0]
eHH[which((numCounterattackRetaliation == 0) | (numReceivedCounterattackRetaliation == 0)),
    totalLostFightRetaliation := 0]
eHH[which(numCounterattackRetaliation == 0), numCounterattackRetaliation := 1]
eHH[which(numReceivedCounterattackRetaliation == 0), numReceivedCounterattackRetaliation := 1]

minFightY <- floor(min(min(eLL[,totalLostFightProtection+totalLostFightRetaliation]/
                             eLL[,numCounterattackRetaliation+numReceivedCounterattackRetaliation],
                           eHH[,totalLostFightProtection+totalLostFightRetaliation]/
                             eHH[,numCounterattackRetaliation+numReceivedCounterattackRetaliation]),
                       min(eLH[,totalLostFightProtection+totalLostFightRetaliation]/
                             eLH[,numCounterattackRetaliation+numReceivedCounterattackRetaliation],
                           eHL[,totalLostFightProtection+totalLostFightRetaliation]/
                             eHL[,numCounterattackRetaliation+numReceivedCounterattackRetaliation])))
maxFightY <- ceiling(max(max(eLL[,totalLostFightProtection+totalLostFightRetaliation]/
                               eLL[,numCounterattackRetaliation+numReceivedCounterattackRetaliation],
                             eHH[,totalLostFightProtection+totalLostFightRetaliation]/
                               eHH[,numCounterattackRetaliation+numReceivedCounterattackRetaliation]),
                         max(eLH[,totalLostFightProtection+totalLostFightRetaliation]/
                               eLH[,numCounterattackRetaliation+numReceivedCounterattackRetaliation],
                             eHL[,totalLostFightProtection+totalLostFightRetaliation]/
                               eHL[,numCounterattackRetaliation+numReceivedCounterattackRetaliation])))

plotLL <- qplot(cycle, (totalLostFightProtection+totalLostFightRetaliation)/
                  (numCounterattackRetaliation+numReceivedCounterattackRetaliation),
                data=eLL, geom = "line",
                ylim = c(minFightY,maxFightY),
                main="LL", xlab="", ylab="Wealth") +
  geom_vline(xintercept=iLL, linetype="dashed", color="red")
plotLH <- qplot(cycle, (totalLostFightProtection+totalLostFightRetaliation)/
                  (numCounterattackRetaliation+numReceivedCounterattackRetaliation),
                data=eLH, geom = "line",
                ylim = c(minFightY,maxFightY),
                main="LH", xlab="", ylab="Wealth") +
  geom_vline(xintercept=iLH, linetype="dashed", color="red")
plotHL <- qplot(cycle, (totalLostFightProtection+totalLostFightRetaliation)/
                  (numCounterattackRetaliation+numReceivedCounterattackRetaliation),
                data=eHL, geom = "line",
                ylim = c(minFightY,maxFightY),
                main="HL", xlab="", ylab="Wealth") +
  geom_vline(xintercept=iHL, linetype="dashed", color="red")
plotHH <- qplot(cycle, (totalLostFightProtection+totalLostFightRetaliation)/
                  (numCounterattackRetaliation+numReceivedCounterattackRetaliation),
                data=eHH, geom = "line",
                ylim = c(minFightY,maxFightY),
                main="HH", xlab="", ylab="Wealth") +
  geom_vline(xintercept=iHH, linetype="dashed", color="red")
png(file=paste(imagePath,"/lossWealthFight.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Loss of Wealth per Fight")
dev.off()


###
### EXTORTER LOSS OF WEALTH ON PUNISHMENT
###
minPunishY <- floor(min(min(eLL[,totalLostPunishment], eHH[,totalLostPunishment]),
                        min(eLH[,totalLostPunishment], eHL[,totalLostPunishment])))
maxPunishY <- ceiling(max(max(eLL[,totalLostPunishment], eHH[,totalLostPunishment]),
                          max(eLH[,totalLostPunishment], eHL[,totalLostPunishment])))

plotLL <- qplot(cycle, totalLostPunishment, data=eLL, geom = "line",
                ylim = c(minPunishY,maxPunishY),
                main="LL", xlab="", ylab="Wealth") +
  geom_vline(xintercept=iLL, linetype="dashed", color="red")
plotLH <- qplot(cycle, totalLostPunishment, data=eLH, geom = "line",
                ylim = c(minPunishY,maxPunishY),
                main="LH", xlab="", ylab="Wealth") +
  geom_vline(xintercept=iLH, linetype="dashed", color="red")
plotHL <- qplot(cycle, totalLostPunishment, data=eHL, geom = "line",
                ylim = c(minPunishY,maxPunishY),
                main="HL", xlab="", ylab="Wealth") +
  geom_vline(xintercept=iHL, linetype="dashed", color="red")
plotHH <- qplot(cycle, totalLostPunishment, data=eHH, geom = "line",
                ylim = c(minPunishY,maxPunishY),
                main="HH", xlab="", ylab="Wealth") +
  geom_vline(xintercept=iHH, linetype="dashed", color="red")
png(file=paste(imagePath,"/lossWealthPunishment.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Loss of Wealth on Punishment")
dev.off()


###
### EXTORTER PUNISHMENT
###
eLL <- extorterSum[which(type == 0), ]
eLH <- extorterSum[which(type == 1), ]
eHL <- extorterSum[which(type == 2), ]
eHH <- extorterSum[which(type == 3), ]

if(iLL <= NROW(eLL)){
  eLL <- eLL[iLL:NROW(eLL), numPunishment := 0]
}
if(iLH <= NROW(eLH)){
  eLH <- eLH[iLH:NROW(eLH), numPunishment := 0]
}
if(iHL <= NROW(eHL)){
  eHL <- eHL[iHL:NROW(eHL), numPunishment := 0]
}
if(iHH <= NROW(eHH)){
  eHH <- eHH[iHH:NROW(eHH), numPunishment := 0]
}

minPunishY <- floor(min(min(eLL[,numPunishment], eHH[,numPunishment]),
                        min(eLH[,numPunishment], eHL[,numPunishment])))
maxPunishY <- ceiling(max(max(eLL[,numPunishment], eHH[,numPunishment]),
                          max(eLH[,numPunishment], eHL[,numPunishment])))

plotLL <- qplot(cycle, numPunishment, data=eLL, geom = "line",
                ylim = c(minPunishY,maxPunishY),
                main="LL", xlab="", ylab="Number of Punishments") +
  geom_vline(xintercept=iLL, linetype="dashed", color="red")
plotLH <- qplot(cycle, numPunishment, data=eLH, geom = "line",
                ylim = c(minPunishY,maxPunishY),
                main="LH", xlab="", ylab="Number of Punishments") +
  geom_vline(xintercept=iLH, linetype="dashed", color="red")
plotHL <- qplot(cycle, numPunishment, data=eHL, geom = "line",
                ylim = c(minPunishY,maxPunishY),
                main="HL", xlab="", ylab="Number of Punishments") +
  geom_vline(xintercept=iHL, linetype="dashed", color="red")
plotHH <- qplot(cycle, numPunishment, data=eHH, geom = "line",
                ylim = c(minPunishY,maxPunishY),
                main="HH", xlab="", ylab="Number of Punishments") +
  geom_vline(xintercept=iHH, linetype="dashed", color="red")
png(file=paste(imagePath,"/numberPunishment.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Extorters Number of Punishment")
dev.off()


###
### EXTORTER PUNISHMENT HIGHLIGHT 1:100
###
minPunishY <- 0
maxPunishY <- ceiling(max(max(eLL[1:100,numPunishment], eHH[1:100,numPunishment]),
                          max(eLH[1:100,numPunishment], eHL[1:100,numPunishment])))

plotLL <- qplot(cycle, numPunishment, data=eLL[1:100], geom = "line",
                ylim = c(minPunishY,maxPunishY),
                main="LL", xlab="", ylab="Number of Punishments")
plotLH <- qplot(cycle, numPunishment, data=eLH[1:100], geom = "line",
                ylim = c(minPunishY,maxPunishY),
                main="LH", xlab="", ylab="Number of Punishments")
plotHL <- qplot(cycle, numPunishment, data=eHL[1:100], geom = "line",
                ylim = c(minPunishY,maxPunishY),
                main="HL", xlab="", ylab="Number of Punishments")
plotHH <- qplot(cycle, numPunishment, data=eHH[1:100], geom = "line",
                ylim = c(minPunishY,maxPunishY),
                main="HH", xlab="", ylab="Number of Punishments")
png(file=paste(imagePath,"/numberPunishmentHighlighted:1-100.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Extorters Number of Punishments [1:100]")
dev.off()


###
### EXTORTER PUNISHMENT HIGHLIGHT 100:END
###
minPunishY <- 0
maxPunishY <- ceiling(max(max(eLL[100:NROW(eLL),numPunishment],
                              eHH[100:NROW(eHH),numPunishment]),
                          max(eLH[100:NROW(eLH),numPunishment],
                              eHL[100:NROW(eHL),numPunishment])))

plotLL <- qplot(cycle, numPunishment, data=eLL[100:NROW(eLL)], geom = "line",
                ylim = c(minPunishY,maxPunishY),
                main="LL", xlab="", ylab="Number of Punishments")
plotLH <- qplot(cycle, numPunishment, data=eLH[100:NROW(eLH)], geom = "line",
                ylim = c(minPunishY,maxPunishY),
                main="LH", xlab="", ylab="Number of Punishments")
plotHL <- qplot(cycle, numPunishment, data=eHL[100:NROW(eHL)], geom = "line",
                ylim = c(minPunishY,maxPunishY),
                main="HL", xlab="", ylab="Number of Punishments")
plotHH <- qplot(cycle, numPunishment, data=eHH[100:NROW(eHH)], geom = "line",
                ylim = c(minPunishY,maxPunishY),
                main="HH", xlab="", ylab="Number of Punishments")
png(file=paste(imagePath,"/numberPunishmentHighlighted:100-END.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Extorters Number of Punishments [100:END]")
dev.off()


###
### EXTORTER FIGHT
###
eLL <- extorterSum[which(type == 0), ]
eLH <- extorterSum[which(type == 1), ]
eHL <- extorterSum[which(type == 2), ]
eHH <- extorterSum[which(type == 3), ]

if(iLL <= NROW(eLL)){
  eLL <- eLL[iLL:NROW(eLL), numCounterattackProtection := 0]
  eLL <- eLL[iLL:NROW(eLL), numCounterattackRetaliation := 0]
  eLL <- eLL[iLL:NROW(eLL), numReceivedCounterattackProtection := 0]
  eLL <- eLL[iLL:NROW(eLL), numReceivedCounterattackRetaliation := 0]
}
if(iLH <= NROW(eLH)){
  eLH <- eLH[iLH:NROW(eLH), numCounterattackProtection := 0]
  eLH <- eLH[iLH:NROW(eLH), numCounterattackRetaliation := 0]
  eLH <- eLH[iLH:NROW(eLH), numReceivedCounterattackProtection := 0]
  eLH <- eLH[iLH:NROW(eLH), numReceivedCounterattackRetaliation := 0]
}
if(iHL <= NROW(eHL)){
  eHL <- eHL[iHL:NROW(eHL), numCounterattackProtection := 0]
  eHL <- eHL[iHL:NROW(eHL), numCounterattackRetaliation := 0]
  eHL <- eHL[iHL:NROW(eHL), numReceivedCounterattackProtection := 0]
  eHL <- eHL[iHL:NROW(eHL), numReceivedCounterattackRetaliation := 0]
}
if(iHH <= NROW(eHH)){
  eHH <- eHH[iHH:NROW(eHH), numCounterattackProtection := 0]
  eHH <- eHH[iHH:NROW(eHH), numCounterattackRetaliaion := 0]
  eHH <- eHH[iHH:NROW(eHH), numReceivedCounterattackProtection := 0]
  eHH <- eHH[iHH:NROW(eHH), numReceivedCounterattackRetaliation := 0]
}

minFightY <- floor(min(min(eLL[,numCounterattackProtection+
                                 numCounterattackRetaliation+
                                 numReceivedCounterattackProtection+
                                 numReceivedCounterattackRetaliation],
                           eHH[,numCounterattackProtection+
                                 numCounterattackRetaliation+
                                 numReceivedCounterattackProtection+
                                 numReceivedCounterattackRetaliation]),
                       min(eLH[,numCounterattackProtection+
                                 numCounterattackRetaliation+
                                 numReceivedCounterattackProtection+
                                 numReceivedCounterattackRetaliation],
                           eHL[,numCounterattackProtection+
                                 numCounterattackRetaliation+
                                 numReceivedCounterattackProtection+
                                 numReceivedCounterattackRetaliation])))
maxFightY <- ceiling(max(max(eLL[,numCounterattackProtection+
                                   numCounterattackRetaliation+
                                   numReceivedCounterattackProtection+
                                   numReceivedCounterattackRetaliation],
                             eHH[,numCounterattackProtection+
                                   numCounterattackRetaliation+
                                   numReceivedCounterattackProtection+
                                   numReceivedCounterattackRetaliation]),
                         max(eLH[,numCounterattackProtection+
                                   numCounterattackRetaliation+
                                   numReceivedCounterattackProtection+
                                   numReceivedCounterattackRetaliation],
                             eHL[,numCounterattackProtection+
                                   numCounterattackRetaliation+
                                   numReceivedCounterattackProtection+
                                   numReceivedCounterattackRetaliation])))

plotLL <- qplot(cycle, numCounterattackProtection+numCounterattackRetaliation+
                  numReceivedCounterattackProtection+numReceivedCounterattackRetaliation,
                data=eLL, geom = "line",
                ylim = c(minFightY,maxFightY),
                main="LL", xlab="", ylab="Number of Fights") +
  geom_vline(xintercept=iLL, linetype="dashed", color="red")
plotLH <- qplot(cycle, numCounterattackProtection+numCounterattackRetaliation+
                  numReceivedCounterattackProtection+numReceivedCounterattackRetaliation,
                data=eLH, geom = "line",
                ylim = c(minFightY,maxFightY),
                main="LH", xlab="", ylab="Number of Fights") +
  geom_vline(xintercept=iLH, linetype="dashed", color="red")
plotHL <- qplot(cycle, numCounterattackProtection+numCounterattackRetaliation+
                  numReceivedCounterattackProtection+numReceivedCounterattackRetaliation,
                data=eHL, geom = "line",
                ylim = c(minFightY,maxFightY),
                main="HL", xlab="", ylab="Number of Fights") +
  geom_vline(xintercept=iHL, linetype="dashed", color="red")
plotHH <- qplot(cycle, numCounterattackProtection+numCounterattackRetaliation+
                  numReceivedCounterattackProtection+numReceivedCounterattackRetaliation,
                data=eHH, geom = "line",
                ylim = c(minFightY,maxFightY),
                main="HH", xlab="", ylab="Number of Fights") +
  geom_vline(xintercept=iHH, linetype="dashed", color="red")
png(file=paste(imagePath,"/numberFight.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Extorters Number of Fights")
dev.off()


###
### EXTORTER FIGHT HIGHLIGHT 1:100
###
minFightY <- 0
maxFightY <- ceiling(max(max(eLL[1:100,numCounterattackProtection+
                                   numCounterattackRetaliation+
                                   numReceivedCounterattackProtection+
                                   numReceivedCounterattackRetaliation],
                             eHH[1:100,numCounterattackProtection+
                                   numCounterattackRetaliation+
                                   numReceivedCounterattackProtection+
                                   numReceivedCounterattackRetaliation]),
                         max(eLH[1:100,numCounterattackProtection+
                                   numCounterattackRetaliation+
                                   numReceivedCounterattackProtection+
                                   numReceivedCounterattackRetaliation],
                             eHL[1:100,numCounterattackProtection+
                                   numCounterattackRetaliation+
                                   numReceivedCounterattackProtection+
                                   numReceivedCounterattackRetaliation])))

plotLL <- qplot(cycle, numCounterattackProtection+numCounterattackRetaliation+
                  numReceivedCounterattackProtection+numReceivedCounterattackRetaliation,
                data=eLL[1:100], geom = "line",
                ylim = c(minFightY,maxFightY),
                main="LL", xlab="", ylab="Number of Fights")
plotLH <- qplot(cycle, numCounterattackProtection+numCounterattackRetaliation+
                  numReceivedCounterattackProtection+numReceivedCounterattackRetaliation,
                data=eLH[1:100], geom = "line",
                ylim = c(minFightY,maxFightY),
                main="LH", xlab="", ylab="Number of Fights")
plotHL <- qplot(cycle, numCounterattackProtection+numCounterattackRetaliation+
                  numReceivedCounterattackProtection+numReceivedCounterattackRetaliation,
                data=eHL[1:100], geom = "line",
                ylim = c(minFightY,maxFightY),
                main="HL", xlab="", ylab="Number of Fights")
plotHH <- qplot(cycle, numCounterattackProtection+numCounterattackRetaliation+
                  numReceivedCounterattackProtection+numReceivedCounterattackRetaliation,
                data=eHH[1:100], geom = "line",
                ylim = c(minFightY,maxFightY),
                main="HH", xlab="", ylab="Number of Fights")
png(file=paste(imagePath,"/numberFightHighlighted:1-100.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Extorters Number of Fights [1:100]")
dev.off()


###
### EXTORTER FIGHT HIGHLIGHT 100:END
###
minFightY <- 0
maxFightY <- ceiling(max(max(eLL[100:NROW(eLL),numCounterattackProtection+
                                   numCounterattackRetaliation+
                                   numReceivedCounterattackProtection+
                                   numReceivedCounterattackRetaliation],
                             eHH[100:NROW(eHH),numCounterattackProtection+
                                   numCounterattackRetaliation+
                                   numReceivedCounterattackProtection+
                                   numReceivedCounterattackRetaliation]),
                         max(eLH[100:NROW(eLH),numCounterattackProtection+
                                   numCounterattackRetaliation+
                                   numReceivedCounterattackProtection+
                                   numReceivedCounterattackRetaliation],
                             eHL[100:NROW(eHL),numCounterattackProtection+
                                   numCounterattackRetaliation+
                                   numReceivedCounterattackProtection+
                                   numReceivedCounterattackRetaliation])))

plotLL <- qplot(cycle, numCounterattackProtection+numCounterattackRetaliation+
                  numReceivedCounterattackProtection+numReceivedCounterattackRetaliation,
                data=eLL[100:NROW(eLL)], geom = "line",
                ylim = c(minFightY,maxFightY),
                main="LL", xlab="", ylab="Number of Fights") +
  geom_vline(xintercept=iLL, linetype="dashed", color="red")
plotLH <- qplot(cycle, numCounterattackProtection+numCounterattackRetaliation+
                  numReceivedCounterattackProtection+numReceivedCounterattackRetaliation,
                data=eLH[100:NROW(eLH)], geom = "line",
                ylim = c(minFightY,maxFightY),
                main="LH", xlab="", ylab="Number of Fights") +
  geom_vline(xintercept=iLH, linetype="dashed", color="red")
plotHL <- qplot(cycle, numCounterattackProtection+numCounterattackRetaliation+
                  numReceivedCounterattackProtection+numReceivedCounterattackRetaliation,
                data=eHL[100:NROW(eHL)], geom = "line",
                ylim = c(minFightY,maxFightY),
                main="HL", xlab="", ylab="Number of Fights") +
  geom_vline(xintercept=iHL, linetype="dashed", color="red")
plotHH <- qplot(cycle, numCounterattackProtection+numCounterattackRetaliation+
                  numReceivedCounterattackProtection+numReceivedCounterattackRetaliation,
                data=eHH[100:NROW(eHH)], geom = "line",
                ylim = c(minFightY,maxFightY),
                main="HH", xlab="", ylab="Number of Fights") +
  geom_vline(xintercept=iHH, linetype="dashed", color="red")
png(file=paste(imagePath,"/numberFightHighlighted:100-END.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Extorters Number of Fights [100:END]")
dev.off()