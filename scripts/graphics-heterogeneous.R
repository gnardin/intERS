###
### Required libraries
###
library(data.table)
library(ggplot2)
library(gridExtra)

###
### Raw simulation data output directory
###
rm(list=ls())
outputPath <- "/data/workspace/repast/intERS/output/30-enlarge-80-counterattack-20/heterogeneous/LL-LH-HL-HH"

###
### Images repository directory
###
dir.create(file.path(outputPath, "images"), showWarnings = FALSE)
imagePath <- paste(outputPath,"/images", sep="")

###
### Upload Consolidated Information
###
esLL <- data.table(read.csv(paste(outputPath,"/cSumExtortersLL.csv",sep=""),sep=";"))
setkey(esLL, cycle)
esLH <- data.table(read.csv(paste(outputPath,"/cSumExtortersLH.csv",sep=""),sep=";"))
setkey(esLH, cycle)
esHL <- data.table(read.csv(paste(outputPath,"/cSumExtortersHL.csv",sep=""),sep=";"))
setkey(esHL, cycle)
esHH <- data.table(read.csv(paste(outputPath,"/cSumExtortersHH.csv",sep=""),sep=";"))
setkey(esHH, cycle)

emLL <- data.table(read.csv(paste(outputPath,"/cAvgExtortersLL.csv",sep=""),sep=";"))
setkey(emLL, cycle)
emLH <- data.table(read.csv(paste(outputPath,"/cAvgExtortersLH.csv",sep=""),sep=";"))
setkey(emLH, cycle)
emHL <- data.table(read.csv(paste(outputPath,"/cAvgExtortersHL.csv",sep=""),sep=";"))
setkey(emHL, cycle)
emHH <- data.table(read.csv(paste(outputPath,"/cAvgExtortersHH.csv",sep=""),sep=";"))
setkey(emHH, cycle)

os <- data.table(read.csv(paste(outputPath,"/","cSumObservers.csv",sep=""), sep=";"))
setkey(os, cycle)

om <- data.table(read.csv(paste(outputPath,"/","cAvgObservers.csv",sep=""), sep=";"))
setkey(om, cycle)

ts <- data.table(read.csv(paste(outputPath,"/","cSumTargets.csv",sep=""), sep=";"))
setkey(ts, cycle)

tm <- data.table(read.csv(paste(outputPath,"/","cAvgTargets.csv",sep=""), sep=";"))
setkey(tm, cycle)


###
### NUMBER OF TARGETS
###
o <- os[, lapply(.SD,mean), by=cycle]

minTotalTargetsY <- floor(min(o[,totalTargets]))
maxTotalTargetsY <- ceiling(max(o[,totalTargets]))

png(file=paste(imagePath,"/numberTargets.png", sep=""), width=800, height=600)
qplot(cycle, totalTargets, data=o, geom = "line", ylim = c(minTotalTargetsY,maxTotalTargetsY),
      main="Number of Targets", xlab="", ylab="Number")
dev.off()


###
### NUMBER OF TARGETS MINIMUM EQUAL 0
###
o <- os[, lapply(.SD,mean), by=cycle]

minTotalTargetsY <- 0 # floor(min(o[,totalTargets]))
maxTotalTargetsY <- ceiling(max(o[,totalTargets]))

png(file=paste(imagePath,"/numberTargetsMin0.png", sep=""), width=800, height=600)
qplot(cycle, totalTargets, data=o, geom = "line", ylim = c(minTotalTargetsY,maxTotalTargetsY),
      main="Number of Targets", xlab="", ylab="Number")
dev.off()


###
### TARGETS ACCUMULATED WEALTH
####
t <- ts[, lapply(.SD,mean), by=cycle]

minWealthY <- floor(min(t[,wealth]))
maxWealthY <- ceiling(max(t[,wealth]))

png(file=paste(imagePath,"/accumulatedTargetsWealth.png", sep=""), width=800, height=600)
qplot(cycle, wealth, data=t, geom = "line", ylim = c(minWealthY,maxWealthY),
      main="Targets Accumulated Wealth", xlab="", ylab="Wealth")
dev.off()


###
### ALL TARGETS AVERAGE INCOME
###
o <- os[, lapply(.SD,mean), by=cycle]
maxTotalTargets <- max(o[,totalTargets])

t <- ts[, lapply(.SD,mean), by=cycle]

minIncomeY <- floor(min(t[,income]/maxTotalTargets))
maxIncomeY <- ceiling(max(t[,income]/maxTotalTargets))

png(file=paste(imagePath,"/averageAllTargetsIncome.png", sep=""), width=800, height=600)
qplot(cycle, income/maxTotalTargets, data=t, geom = "line", ylim = c(minIncomeY,maxIncomeY),
      main="All Targets Average Income", xlab="", ylab="Wealth")
dev.off()


###
### AVERAGE TARGETS INCOME PERCENTAGE PAID ON EXTORTION
###
t <- tm[, lapply(.SD,mean), by=cycle]

minIncomeY <- floor(min(t[,totalPaid]/t[,income+totalPaid+totalPunishment]))
maxIncomeY <- ceiling(max(t[,totalPaid]/t[,income+totalPaid+totalPunishment]))

png(file=paste(imagePath,"/averageAllTargetsExtortedIncome.png", sep=""), width=800, height=600)
qplot(cycle, totalPaid/(income+totalPaid+totalPunishment), data=t, geom = "line",
      ylim = c(minIncomeY,maxIncomeY), main="Average Targets Income Percentage Paid on Extortion",
      xlab="", ylab="Percentage")
dev.off()


###
### NUMBER OF EXTORTERS TOTAL
###
o <- os[, lapply(.SD,mean), by=cycle]

minTotalExtortersY <- 0 # floor(min(o[,totalExtortersFree+totalExtortersImprisoned]))
maxTotalExtortersY <- ceiling(max(o[,totalExtortersFree+totalExtortersImprisoned]))

png(file=paste(imagePath,"/numberExtortersTotal.png", sep=""), width=800, height=600)
qplot(cycle, totalExtortersFree+totalExtortersImprisoned, data=o, geom = "line",
      ylim = c(minTotalExtortersY,maxTotalExtortersY),
      main="Total Number of Extorters", xlab="", ylab="Number")
dev.off()


###
### NUMBER OF EXTORTERS BY TYPE
###
o <- os[, lapply(.SD,mean), by=cycle]

minExtortersY <- 0 # floor(min(min(o[,FLL+ILL],o[,FHH+IHH]),
#       min(o[,FLH+ILH],o[,FHL+IHL])))
maxExtortersY <- ceiling(max(max(o[,F0+I0],o[,F3+I3]),
                             max(o[,F1+I1],o[,F2+I2])))

plotLL <- qplot(cycle, F0+I0, data=o, geom = "line",
                ylim = c(minExtortersY,maxExtortersY),
                main="LL", xlab="", ylab="Number")
plotLH <- qplot(cycle, F1+I1, data=o, geom = "line",
                ylim = c(minExtortersY,maxExtortersY),
                main="LH", xlab="", ylab="Number")
plotHL <- qplot(cycle, F2+I2, data=o, geom = "line",
                ylim = c(minExtortersY,maxExtortersY),
                main="HL", xlab="", ylab="Number")
plotHH <- qplot(cycle, F3+I3, data=o, geom = "line",
                ylim = c(minExtortersY,maxExtortersY),
                main="HH", xlab="", ylab="Number")
png(file=paste(imagePath,"/numberExtortersType.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Number of Extorters")
dev.off()


###
### EXTORTER ACCUMULATED WEALTH
###
eLL <- esLL[, lapply(.SD,mean), by=cycle]
eLH <- esLH[, lapply(.SD,mean), by=cycle]
eHL <- esHL[, lapply(.SD,mean), by=cycle]
eHH <- esHH[, lapply(.SD,mean), by=cycle]

minWealthY <- floor(min(min(eLL[,wealth],eHH[,wealth]),min(eLH[,wealth],eHL[,wealth])))
maxWealthY <- ceiling(max(max(eLL[,wealth],eHH[,wealth]),max(eLH[,wealth],eHL[,wealth])))

plotLL <- qplot(cycle, wealth, data=eLL, geom = "line", ylim = c(minWealthY,maxWealthY),
                main="LL", xlab="", ylab="Wealth")
plotLH <- qplot(cycle, wealth, data=eLH, geom = "line", ylim = c(minWealthY,maxWealthY),
                main="LH", xlab="", ylab="Wealth")
plotHL <- qplot(cycle, wealth, data=eHL, geom = "line", ylim = c(minWealthY,maxWealthY),
                main="HL", xlab="", ylab="Wealth")
plotHH <- qplot(cycle, wealth, data=eHH, geom = "line", ylim = c(minWealthY,maxWealthY),
                main="HH", xlab="", ylab="Wealth")
png(file=paste(imagePath,"/accumulatedExtortersWealth.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Extorters Accumulated Wealth")
dev.off()


###
### ALL EXTORTERS AVERAGE WEALTH
###
o <- os[, lapply(.SD,mean), by=cycle]
maxTotalExtorters <- max(o[,totalExtortersFree+totalExtortersImprisoned])

eLL <- emLL[, lapply(.SD,mean), by=cycle]
eLH <- emLH[, lapply(.SD,mean), by=cycle]
eHL <- emHL[, lapply(.SD,mean), by=cycle]
eHH <- emHH[, lapply(.SD,mean), by=cycle]

minWealthY <- floor(min(min(eLL[,wealth/maxTotalExtorters],eHH[,wealth/maxTotalExtorters]),
                        min(eLH[,wealth/maxTotalExtorters],eHL[,wealth/maxTotalExtorters])))
maxWealthY <- ceiling(max(max(eLL[,wealth/maxTotalExtorters],eHH[,wealth/maxTotalExtorters]),
                          max(eLH[,wealth/maxTotalExtorters],eHL[,wealth/maxTotalExtorters])))

plotLL <- qplot(cycle, wealth/maxTotalExtorters, data=eLL, geom = "line", ylim = c(minWealthY,maxWealthY),
                main="LL", xlab="", ylab="Wealth")
plotLH <- qplot(cycle, wealth/maxTotalExtorters, data=eLH, geom = "line", ylim = c(minWealthY,maxWealthY),
                main="LH", xlab="", ylab="Wealth")
plotHL <- qplot(cycle, wealth/maxTotalExtorters, data=eHL, geom = "line", ylim = c(minWealthY,maxWealthY),
                main="HL", xlab="", ylab="Wealth")
plotHH <- qplot(cycle, wealth/maxTotalExtorters, data=eHH, geom = "line", ylim = c(minWealthY,maxWealthY),
                main="HH", xlab="", ylab="Wealth")
png(file=paste(imagePath,"/averageAllExtortersWealth.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Extorters Average Wealth")
dev.off()


###
### NUMBER OF EXTORTIONS
###
eLL <- esLL[, lapply(.SD,mean), by=cycle]
eLH <- esLH[, lapply(.SD,mean), by=cycle]
eHL <- esHL[, lapply(.SD,mean), by=cycle]
eHH <- esHH[, lapply(.SD,mean), by=cycle]

minNumExtortionY <- 0 # floor(min(min(eLL[,numExtortion],eHH[,numExtortion]),
#        min(eLH[,numExtortion],eHL[,numExtortion])))
maxNumExtortionY <- ceiling(max(max(eLL[,numExtortion],eHH[,numExtortion]),
                                max(eLH[,numExtortion],eHL[,numExtortion])))

plotLL <- qplot(cycle, numExtortion, data=eLL, geom = "line", ylim = c(minNumExtortionY,maxNumExtortionY),
                main="LL", xlab="", ylab="Number")
plotLH <- qplot(cycle, numExtortion, data=eLH, geom = "line", ylim = c(minNumExtortionY,maxNumExtortionY),
                main="LH", xlab="", ylab="Number")
plotHL <- qplot(cycle, numExtortion, data=eHL, geom = "line", ylim = c(minNumExtortionY,maxNumExtortionY),
                main="HL", xlab="", ylab="Number")
plotHH <- qplot(cycle, numExtortion, data=eHH, geom = "line", ylim = c(minNumExtortionY,maxNumExtortionY),
                main="HH", xlab="", ylab="Number")
png(file=paste(imagePath,"/numberExtortions.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Number of Extortions")
dev.off()


###
### NUMBER OF TARGETS PER EXTORTER
###
eLL <- emLL[, lapply(.SD,mean), by=cycle]
eLH <- emLH[, lapply(.SD,mean), by=cycle]
eHL <- emHL[, lapply(.SD,mean), by=cycle]
eHH <- emHH[, lapply(.SD,mean), by=cycle]

minNumTargetsY <- 0 # floor(min(min(eLL[,numTargets],eHH[,numTargets]),
#        min(eLH[,numTargets],eHL[,numTargets])))
maxNumTargetsY <- ceiling(max(max(eLL[,numTargets],eHH[,numTargets]),
                              max(eLH[,numTargets],eHL[,numTargets])))

plotLL <- qplot(cycle, numTargets, data=eLL, geom = "line", ylim = c(minNumTargetsY,maxNumTargetsY),
                main="LL", xlab="", ylab="Number")
plotLH <- qplot(cycle, numTargets, data=eLH, geom = "line", ylim = c(minNumTargetsY,maxNumTargetsY),
                main="LH", xlab="", ylab="Number")
plotHL <- qplot(cycle, numTargets, data=eHL, geom = "line", ylim = c(minNumTargetsY,maxNumTargetsY),
                main="HL", xlab="", ylab="Number")
plotHH <- qplot(cycle, numTargets, data=eHH, geom = "line", ylim = c(minNumTargetsY,maxNumTargetsY),
                main="HH", xlab="", ylab="Number")
png(file=paste(imagePath,"/numberTargetsPerExtorter.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Number of Targets per Extorter")
dev.off()


###
### NUMBER OF EXTORTION PER TARGET
###
o <- os[, lapply(.SD,mean), by=cycle]
maxTotalTargetsLL <- max(o[,totalTargets])
maxTotalTargetsLH <- max(o[,totalTargets])
maxTotalTargetsHL <- max(o[,totalTargets])
maxTotalTargetsHH <- max(o[,totalTargets])

eLL <- esLL[, lapply(.SD,mean), by=cycle]
eLH <- esLH[, lapply(.SD,mean), by=cycle]
eHL <- esHL[, lapply(.SD,mean), by=cycle]
eHH <- esHH[, lapply(.SD,mean), by=cycle]

minNumExtortionY <- 0 # floor(min(min(eLL[,numExtortion/maxTotalTargetsLL],
#            eHH[,numExtortion/maxTotalTargetsHH]),
#        min(eLH[,numExtortion/maxTotalTargetsLH],
#            eHL[,numExtortion/maxTotalTargetsHL])))
maxNumExtortionY <- ceiling(max(max(eLL[,numExtortion/maxTotalTargetsLL],
                                    eHH[,numExtortion/maxTotalTargetsHH]),
                                max(eLH[,numExtortion/maxTotalTargetsLH],
                                    eHL[,numExtortion/maxTotalTargetsHL])))

plotLL <- qplot(cycle, numExtortion/maxTotalTargetsLL, data=eLL, geom = "line", 
                ylim = c(minNumExtortionY,maxNumExtortionY),
                main="LL", xlab="", ylab="Number")
plotLH <- qplot(cycle, numExtortion/maxTotalTargetsLH, data=eLH, geom = "line",
                ylim = c(minNumExtortionY,maxNumExtortionY),
                main="LH", xlab="", ylab="Number")
plotHL <- qplot(cycle, numExtortion/maxTotalTargetsHL, data=eHL, geom = "line",
                ylim = c(minNumExtortionY,maxNumExtortionY),
                main="HL", xlab="", ylab="Number")
plotHH <- qplot(cycle, numExtortion/maxTotalTargetsHH, data=eHH, geom = "line",
                ylim = c(minNumExtortionY,maxNumExtortionY),
                main="HH", xlab="", ylab="Number")
png(file=paste(imagePath,"/numberExtortionsPerTarget.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Number of Extortions per Target")
dev.off()


###
### EXTORTER VIOLENCE
###
eLL <- esLL[, lapply(.SD,mean), by=cycle]
eLH <- esLH[, lapply(.SD,mean), by=cycle]
eHL <- esHL[, lapply(.SD,mean), by=cycle]
eHH <- esHH[, lapply(.SD,mean), by=cycle]

minViolenceY <- floor(min(min(eLL[,numCounterattack+numPunishment],
                              eHH[,numCounterattack+numPunishment]),
                          min(eLH[,numCounterattack+numPunishment],
                              eHL[,numCounterattack+numPunishment])))
maxViolenceY <- ceiling(max(max(eLL[,numCounterattack+numPunishment],
                                eHH[,numCounterattack+numPunishment]),
                            max(eLH[,numCounterattack+numPunishment],
                                eHL[,numCounterattack+numPunishment])))

plotLL <- qplot(cycle, numCounterattack+numPunishment, data=eLL, geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="LL", xlab="", ylab="Number of Fights + Punishments")
plotLH <- qplot(cycle, numCounterattack+numPunishment, data=eLH, geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="LH", xlab="", ylab="Number of Fights + Punishments")
plotHL <- qplot(cycle, numCounterattack+numPunishment, data=eHL, geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="HL", xlab="", ylab="Number of Fights + Punishments")
plotHH <- qplot(cycle, numCounterattack+numPunishment, data=eHH, geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="HH", xlab="", ylab="Number of Fights + Punishments")
png(file=paste(imagePath,"/numberViolence.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Extorters Number of Violent Activities")
dev.off()


###
### EXTORTER VIOLENCE HIGHLIGHT 1:50
###
minViolenceY <- 0 # floor(min(min(eLL[1:50,numCounterattack+numPunishment],
#            eHH[1:50,numCounterattack+numPunishment]),
#        min(eLH[1:50,numCounterattack+numPunishment],
#            eHL[1:50,numCounterattack+numPunishment])))
maxViolenceY <- ceiling(max(max(eLL[1:50,numCounterattack+numPunishment],
                                eHH[1:50,numCounterattack+numPunishment]),
                            max(eLH[1:50,numCounterattack+numPunishment],
                                eHL[1:50,numCounterattack+numPunishment])))

plotLL <- qplot(cycle, numCounterattack+numPunishment, data=eLL[1:50], geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="LL", xlab="", ylab="Number of Fights + Punishments")
plotLH <- qplot(cycle, numCounterattack+numPunishment, data=eLH[1:50], geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="LH", xlab="", ylab="Number of Fights + Punishments")
plotHL <- qplot(cycle, numCounterattack+numPunishment, data=eHL[1:50], geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="HL", xlab="", ylab="Number of Fights + Punishments")
plotHH <- qplot(cycle, numCounterattack+numPunishment, data=eHH[1:50], geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="HH", xlab="", ylab="Number of Fights + Punishments")
png(file=paste(imagePath,"/numberViolenceHighlighted:1-50.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Number of Violent Activities [1:50]")
dev.off()


###
### EXTORTER VIOLENCE HIGHLIGHT 50:END
###
minViolenceY <- 0 # floor(min(min(eLL[50:NROW(eLL),numCounterattack+numPunishment],
#            eHH[50:NROW(eHH),numCounterattack+numPunishment]),
#        min(eLH[50:NROW(eLH),numCounterattack+numPunishment],
#            eHL[50:NROW(eHL),numCounterattack+numPunishment])))
maxViolenceY <- ceiling(max(max(eLL[50:NROW(eLL),numCounterattack+numPunishment],
                                eHH[50:NROW(eHH),numCounterattack+numPunishment]),
                            max(eLH[50:NROW(eLH),numCounterattack+numPunishment],
                                eHL[50:NROW(eHL),numCounterattack+numPunishment])))

plotLL <- qplot(cycle, numCounterattack+numPunishment, data=eLL[50:NROW(eLL)], geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="LL", xlab="", ylab="Number of Fights + Punishments")
plotLH <- qplot(cycle, numCounterattack+numPunishment, data=eLH[50:NROW(eLH)], geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="LH", xlab="", ylab="Number of Fights + Punishments")
plotHL <- qplot(cycle, numCounterattack+numPunishment, data=eHL[50:NROW(eHL)], geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="HL", xlab="", ylab="Number of Fights + Punishments")
plotHH <- qplot(cycle, numCounterattack+numPunishment, data=eHH[50:NROW(eHH)], geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="HH", xlab="", ylab="Number of Fights + Punishments")
png(file=paste(imagePath,"/numberViolenceHighlighted:50-END.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Number of Violent Activities [50:END]")
dev.off()


###
### EXTORTER LOSS OF WEALTH ON VIOLENCE
###
eLL <- esLL[, lapply(.SD,mean), by=cycle]
eLH <- esLH[, lapply(.SD,mean), by=cycle]
eHL <- esHL[, lapply(.SD,mean), by=cycle]
eHH <- esHH[, lapply(.SD,mean), by=cycle]

lastLine <- max(max(nrow(eLL),nrow(eHH)),max(nrow(eLH),nrow(eHL)))

for(index in 2:lastLine){
  
  if(index < nrow(eLL)){
    aux <- eLL[index-1,totalLostFight] + eLL[index,totalLostFight]
    eLL[index,totalLostFight := aux]
    aux <- eLL[index-1,totalLostPunishment] + eLL[index,totalLostPunishment]
    eLL[index,totalLostPunishment := aux]
  }
  
  if(index < nrow(eLH)){
    aux <- eLH[index-1,totalLostFight] + eLH[index,totalLostFight]
    eLH[index,totalLostFight := aux]
    aux <- eLH[index-1,totalLostPunishment] + eLH[index,totalLostPunishment]
    eLH[index,totalLostPunishment := aux]
  }
  
  if(index < nrow(eHL)){
    aux <- eHL[index-1,totalLostFight] + eHL[index,totalLostFight]
    eHL[index,totalLostFight := aux]
    aux <- eHL[index-1,totalLostPunishment] + eHL[index,totalLostPunishment]
    eHL[index,totalLostPunishment := aux]
  }
  
  if(index < nrow(eHH)){
    aux <- eHH[index-1,totalLostFight] + eHH[index,totalLostFight]
    eHH[index,totalLostFight := aux]
    aux <- eHH[index-1,totalLostPunishment] + eHH[index,totalLostPunishment]
    eHH[index,totalLostPunishment := aux]
  }
}

minViolenceY <- floor(min(min(eLL[,totalLostFight+totalLostPunishment],
                              eHH[,totalLostFight+totalLostPunishment]),
                          min(eLH[,totalLostFight+totalLostPunishment],
                              eHL[,totalLostFight+totalLostPunishment])))
maxViolenceY <- ceiling(max(max(eLL[,totalLostFight+totalLostPunishment],
                                eHH[,totalLostFight+totalLostPunishment]),
                            max(eLH[,totalLostFight+totalLostPunishment],
                                eHL[,totalLostFight+totalLostPunishment])))

plotLL <- qplot(cycle, totalLostFight+totalLostPunishment, data=eLL, geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="LL", xlab="", ylab="Wealth")
plotLH <- qplot(cycle, totalLostFight+totalLostPunishment, data=eLH, geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="LH", xlab="", ylab="Wealth")
plotHL <- qplot(cycle, totalLostFight+totalLostPunishment, data=eHL, geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="HL", xlab="", ylab="Wealth")
plotHH <- qplot(cycle, totalLostFight+totalLostPunishment, data=eHH, geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="HH", xlab="", ylab="Wealth")
png(file=paste(imagePath,"/lossWealthViolence.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Loss of Wealth on Violence")
dev.off()


###
### EXTORTER LOSS OF WEALTH ON FIGHTING
###
minFightY <- floor(min(min(eLL[,totalLostFight], eHH[,totalLostFight]),
                       min(eLH[,totalLostFight], eHL[,totalLostFight])))
maxFightY <- ceiling(max(max(eLL[,totalLostFight], eHH[,totalLostFight]),
                         max(eLH[,totalLostFight], eHL[,totalLostFight])))

plotLL <- qplot(cycle, totalLostFight, data=eLL, geom = "line",
                ylim = c(minFightY,maxFightY), main="LL", xlab="", ylab="Wealth")
plotLH <- qplot(cycle, totalLostFight, data=eLH, geom = "line",
                ylim = c(minFightY,maxFightY), main="LH", xlab="", ylab="Wealth")
plotHL <- qplot(cycle, totalLostFight, data=eHL, geom = "line",
                ylim = c(minFightY,maxFightY), main="HL", xlab="", ylab="Wealth")
plotHH <- qplot(cycle, totalLostFight, data=eHH, geom = "line",
                ylim = c(minFightY,maxFightY), main="HH", xlab="", ylab="Wealth")
png(file=paste(imagePath,"/lossWealthFight.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Loss of Wealth on Fighting")
dev.off()


###
### EXTORTER LOSS OF WEALTH ON PUNISHMENT
###
minPunishY <- floor(min(min(eLL[,totalLostPunishment], eHH[,totalLostPunishment]),
                        min(eLH[,totalLostPunishment], eHL[,totalLostPunishment])))
maxPunishY <- ceiling(max(max(eLL[,totalLostPunishment], eHH[,totalLostPunishment]),
                          max(eLH[,totalLostPunishment], eHL[,totalLostPunishment])))

plotLL <- qplot(cycle, totalLostPunishment, data=eLL, geom = "line",
                ylim = c(minPunishY,maxPunishY), main="LL", xlab="", ylab="Wealth")
plotLH <- qplot(cycle, totalLostPunishment, data=eLH, geom = "line",
                ylim = c(minPunishY,maxPunishY), main="LH", xlab="", ylab="Wealth")
plotHL <- qplot(cycle, totalLostPunishment, data=eHL, geom = "line",
                ylim = c(minPunishY,maxPunishY), main="HL", xlab="", ylab="Wealth")
plotHH <- qplot(cycle, totalLostPunishment, data=eHH, geom = "line",
                ylim = c(minPunishY,maxPunishY), main="HH", xlab="", ylab="Wealth")
png(file=paste(imagePath,"/lossWealthPunishment.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Loss of Wealth on Punishment")
dev.off()


###
### EXTORTER PUNISHMENT
###
eLL <- esLL[, lapply(.SD,mean), by=cycle]
eLH <- esLH[, lapply(.SD,mean), by=cycle]
eHL <- esHL[, lapply(.SD,mean), by=cycle]
eHH <- esHH[, lapply(.SD,mean), by=cycle]

minPunishY <- floor(min(min(eLL[,numPunishment], eHH[,numPunishment]),
                        min(eLH[,numPunishment], eHL[,numPunishment])))
maxPunishY <- ceiling(max(max(eLL[,numPunishment], eHH[,numPunishment]),
                          max(eLH[,numPunishment], eHL[,numPunishment])))

plotLL <- qplot(cycle, numPunishment, data=eLL, geom = "line",
                ylim = c(minPunishY,maxPunishY),
                main="LL", xlab="", ylab="Number of Punishments")
plotLH <- qplot(cycle, numPunishment, data=eLH, geom = "line",
                ylim = c(minPunishY,maxPunishY),
                main="LH", xlab="", ylab="Number of Punishments")
plotHL <- qplot(cycle, numPunishment, data=eHL, geom = "line",
                ylim = c(minPunishY,maxPunishY),
                main="HL", xlab="", ylab="Number of Punishments")
plotHH <- qplot(cycle, numPunishment, data=eHH, geom = "line",
                ylim = c(minPunishY,maxPunishY),
                main="HH", xlab="", ylab="Number of Punishments")
png(file=paste(imagePath,"/numberPunishment.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Extorters Number of Punishment")
dev.off()


###
### EXTORTER PUNISHMENT HIGHLIGHT 1:50
###
minPunishY <- 0 # floor(min(min(eLL[1:50,numPunishment],
#            eHH[1:50,numPunishment]),
#        min(eLH[1:50,numPunishment],
#            eHL[1:50,numPunishment])))
maxPunishY <- ceiling(max(max(eLL[1:50,numPunishment], eHH[1:50,numPunishment]),
                          max(eLH[1:50,numPunishment], eHL[1:50,numPunishment])))

plotLL <- qplot(cycle, numPunishment, data=eLL[1:50], geom = "line",
                ylim = c(minPunishY,maxPunishY),
                main="LL", xlab="", ylab="Number of Punishments")
plotLH <- qplot(cycle, numPunishment, data=eLH[1:50], geom = "line",
                ylim = c(minPunishY,maxPunishY),
                main="LH", xlab="", ylab="Number of Punishments")
plotHL <- qplot(cycle, numPunishment, data=eHL[1:50], geom = "line",
                ylim = c(minPunishY,maxPunishY),
                main="HL", xlab="", ylab="Number of Punishments")
plotHH <- qplot(cycle, numPunishment, data=eHH[1:50], geom = "line",
                ylim = c(minPunishY,maxPunishY),
                main="HH", xlab="", ylab="Number of Punishments")
png(file=paste(imagePath,"/numberPunishmentHighlighted:1-50.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Extorters Number of Punishments [1:50]")
dev.off()


###
### EXTORTER PUNISHMENT HIGHLIGHT 50:END
###
minPunishY <- 0 # floor(min(min(eLL[50:NROW(eLL),numPunishment],
#            eHH[50:NROW(eHH),numPunishment]),
#        min(eLH[50:NROW(eLH),numPunishment],
#            eHL[50:NROW(eHL),numPunishment])))
maxPunishY <- ceiling(max(max(eLL[50:NROW(eLL),numPunishment], eHH[50:NROW(eHH),numPunishment]),
                          max(eLH[50:NROW(eLH),numPunishment], eHL[50:NROW(eHL),numPunishment])))

plotLL <- qplot(cycle, numPunishment, data=eLL[50:NROW(eLL)], geom = "line",
                ylim = c(minPunishY,maxPunishY),
                main="LL", xlab="", ylab="Number of Punishments")
plotLH <- qplot(cycle, numPunishment, data=eLH[50:NROW(eLH)], geom = "line",
                ylim = c(minPunishY,maxPunishY),
                main="LH", xlab="", ylab="Number of Punishments")
plotHL <- qplot(cycle, numPunishment, data=eHL[50:NROW(eHL)], geom = "line",
                ylim = c(minPunishY,maxPunishY),
                main="HL", xlab="", ylab="Number of Punishments")
plotHH <- qplot(cycle, numPunishment, data=eHH[50:NROW(eHH)], geom = "line",
                ylim = c(minPunishY,maxPunishY),
                main="HH", xlab="", ylab="Number of Punishments")
png(file=paste(imagePath,"/numberPunishmentHighlighted:50-END.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Extorters Number of Punishments [50:END]")
dev.off()


###
### EXTORTER FIGHT
###
eLL <- esLL[, lapply(.SD,mean), by=cycle]
eLH <- esLH[, lapply(.SD,mean), by=cycle]
eHL <- esHL[, lapply(.SD,mean), by=cycle]
eHH <- esHH[, lapply(.SD,mean), by=cycle]

minFightY <- floor(min(min(eLL[,numCounterattack], eHH[,numCounterattack]),
                       min(eLH[,numCounterattack], eHL[,numCounterattack])))
maxFightY <- ceiling(max(max(eLL[,numCounterattack], eHH[,numCounterattack]),
                         max(eLH[,numCounterattack], eHL[,numCounterattack])))

plotLL <- qplot(cycle, numCounterattack, data=eLL, geom = "line",
                ylim = c(minFightY,maxFightY),
                main="LL", xlab="", ylab="Number of Fights")
plotLH <- qplot(cycle, numCounterattack, data=eLH, geom = "line",
                ylim = c(minFightY,maxFightY),
                main="LH", xlab="", ylab="Number of Fights")
plotHL <- qplot(cycle, numCounterattack, data=eHL, geom = "line",
                ylim = c(minFightY,maxFightY),
                main="HL", xlab="", ylab="Number of Fights")
plotHH <- qplot(cycle, numCounterattack, data=eHH, geom = "line",
                ylim = c(minFightY,maxFightY),
                main="HH", xlab="", ylab="Number of Fights")
png(file=paste(imagePath,"/numberFight.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Extorters Number of Fights")
dev.off()


###
### EXTORTER FIGHT HIGHLIGHT 1:50
###
minFightY <- 0 # floor(min(min(eLL[1:50,numCounterattack],
#            eHH[1:50,numCounterattack]),
#        min(eLH[1:50,numCounterattack],
#            eHL[1:50,numCounterattack])))
maxFightY <- ceiling(max(max(eLL[1:50,numCounterattack], eHH[1:50,numCounterattack]),
                         max(eLH[1:50,numCounterattack], eHL[1:50,numCounterattack])))

plotLL <- qplot(cycle, numCounterattack, data=eLL[1:50], geom = "line",
                ylim = c(minFightY,maxFightY),
                main="LL", xlab="", ylab="Number of Fights")
plotLH <- qplot(cycle, numCounterattack, data=eLH[1:50], geom = "line",
                ylim = c(minFightY,maxFightY),
                main="LH", xlab="", ylab="Number of Fights")
plotHL <- qplot(cycle, numCounterattack, data=eHL[1:50], geom = "line",
                ylim = c(minFightY,maxFightY),
                main="HL", xlab="", ylab="Number of Fights")
plotHH <- qplot(cycle, numCounterattack, data=eHH[1:50], geom = "line",
                ylim = c(minFightY,maxFightY),
                main="HH", xlab="", ylab="Number of Fights")
png(file=paste(imagePath,"/numberFightHighlighted:1-50.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Extorters Number of Fights [1:50]")
dev.off()


###
### EXTORTER FIGHT HIGHLIGHT 50:END
###
minFightY <- 0 # floor(min(min(eLL[50:NROW(eLL),numCounterattack],
#            eHH[50:NROW(eHH),numCounterattack]),
#        min(eLH[50:NROW(eLH),numCounterattack],
#            eHL[50:NROW(eHL),numCounterattack])))
maxFightY <- ceiling(max(max(eLL[50:NROW(eLL),numCounterattack], eHH[50:NROW(eHH),numCounterattack]),
                         max(eLH[50:NROW(eLH),numCounterattack], eHL[50:NROW(eHL),numCounterattack])))

plotLL <- qplot(cycle, numCounterattack, data=eLL[50:NROW(eLL)], geom = "line",
                ylim = c(minFightY,maxFightY),
                main="LL", xlab="", ylab="Number of Fights")
plotLH <- qplot(cycle, numCounterattack, data=eLH[50:NROW(eLH)], geom = "line",
                ylim = c(minFightY,maxFightY),
                main="LH", xlab="", ylab="Number of Fights")
plotHL <- qplot(cycle, numCounterattack, data=eHL[50:NROW(eHL)], geom = "line",
                ylim = c(minFightY,maxFightY),
                main="HL", xlab="", ylab="Number of Fights")
plotHH <- qplot(cycle, numCounterattack, data=eHH[50:NROW(eHH)], geom = "line",
                ylim = c(minFightY,maxFightY),
                main="HH", xlab="", ylab="Number of Fights")
png(file=paste(imagePath,"/numberFightHighlighted:50-END.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Extorters Number of Fights [50:END]")
dev.off()


###
### NUMBER OF PAID EXTORTIONS
###
t <- ts[, lapply(.SD,mean), by=cycle]

minNumPaidY <- 0 # floor(min(t[,numPaid]))
maxNumPaidY <- ceiling(max(t[,numPaid]))

png(file=paste(imagePath,"/numberExtortionsPaid.png", sep=""), width=800, height=600)
qplot(cycle, numPaid, data=t, geom = "line", ylim = c(minNumPaidY,maxNumPaidY),
      main="Extortions Paid", xlab="", ylab="Number")
dev.off()


###
### NUMBER OF PAID EXTORTIONS 1-250
###
t <- ts[, lapply(.SD,mean), by=cycle]

minNumPaidY <- 0 # floor(min(t[1:250,numPaid]))
maxNumPaidY <- ceiling(max(t[1:250,numPaid]))

png(file=paste(imagePath,"/numberExtortionsPaid:1-250.png", sep=""), width=800, height=600)
qplot(cycle, numPaid, data=t[1:250,], geom = "line", ylim = c(minNumPaidY,maxNumPaidY),
      main="Extortions Paid", xlab="", ylab="Number")
dev.off()


###
### NUMBER OF NON PAID EXTORTIONS
###
t <- ts[, lapply(.SD,mean), by=cycle]

minNumNotPaidY <- 0 # floor(min(t[,numNotPaid]))
maxNumNotPaidY <- ceiling(max(t[,numNotPaid]))

png(file=paste(imagePath,"/numberExtortionsNotPaid.png", sep=""), width=800, height=600)
qplot(cycle, numNotPaid, data=t, geom = "line", ylim = c(minNumNotPaidY,maxNumNotPaidY),
      main="Extortions Not Paid", xlab="", ylab="Number")
dev.off()


###
### NUMBER OF NON PAID EXTORTIONS 1-250
###
t <- ts[, lapply(.SD,mean), by=cycle]

minNumNotPaidY <- 0 # floor(min(t[1:250,numNotPaid]))
maxNumNotPaidY <- ceiling(max(t[1:250,numNotPaid]))

png(file=paste(imagePath,"/numberExtortionsNotPaid:1-250.png", sep=""), width=800, height=600)
qplot(cycle, numNotPaid, data=t[1:250,], geom = "line", ylim = c(minNumNotPaidY,maxNumNotPaidY),
      main="Extortions Not Paid", xlab="", ylab="Number")
dev.off()

###
### SOCIETY WEALTH LOSSES AND PERCENTAGE
###
t <- ts[, lapply(.SD,mean), by=cycle]

eLL <- esLL[, lapply(.SD,mean), by=cycle]
eLH <- esLH[, lapply(.SD,mean), by=cycle]
eHL <- esHL[, lapply(.SD,mean), by=cycle]
eHH <- esHH[, lapply(.SD,mean), by=cycle]

cycle <- seq(1:nrow(t))
dWealth <- mapply(sum, t[,wealth], eLL[,wealth], eLH[,wealth], eHL[,wealth], eHH[,wealth])
dLosses <- mapply(sum, t[,totalPunishment], eLL[,totalLostFight], eLH[,totalLostFight],
                  eHL[,totalLostFight], eHH[,totalLostFight])
society <- cbind(cycle, dWealth, dLosses)
society <- data.table(society)
setnames(society, c("data","wealth","losses"))

minWealthY <- floor(min(society[,wealth]))
maxWealthY <- ceiling(max(society[,wealth]))

png(file=paste(imagePath,"/societyWealth.png", sep=""), width=800, height=600)
qplot(cycle, wealth, data=society, geom = "line", ylim = c(minWealthY,maxWealthY),
      main="Society Wealth", xlab="", ylab="Wealth")
dev.off()

minLossesY <- floor(min(society[,losses]))
maxLossesY <- ceiling(max(society[,losses]))

png(file=paste(imagePath,"/societyLosses.png", sep=""), width=800, height=600)
qplot(cycle, losses, data=society, geom = "line", ylim = c(minLossesY,maxLossesY),
      main="Society Losses", xlab="", ylab="Losses")
dev.off()

minY <- floor(min(society[,losses/(wealth+losses)]))
maxY <- ceiling(max(society[,losses/(wealth+losses)]))

png(file=paste(imagePath,"/societyLossesPercentage.png", sep=""), width=800, height=600)
qplot(cycle, losses/(wealth+losses), data=society, geom = "line", ylim = c(minY,maxY),
      main="Society Losses Percentage", xlab="", ylab="Percentage")
dev.off()


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
maxY <- 6

plotLL <- qplot(wealth, data=e[which(type=="0")], geom = "histogram",
                xlim = c(minWealthX, maxWealthX), #ylim = c(minY,maxY),
                main="LL", xlab="Wealth", ylab="Number of Extorters")
plotLH <- qplot(wealth, data=e[which(type=="1")], geom = "histogram",
                xlim = c(minWealthX, maxWealthX), #ylim = c(minY,maxY),
                main="LH", xlab="Wealth", ylab="Number of Extorters")
plotHL <- qplot(wealth, data=e[which(type=="2")], geom = "histogram",
                xlim = c(minWealthX, maxWealthX), #ylim = c(minY,maxY),
                main="HL", xlab="Wealth", ylab="Number of Extorters")
plotHH <- qplot(wealth, data=e[which(type=="3")], geom = "histogram",
                xlim = c(minWealthX, maxWealthX), #ylim = c(minY,maxY),
                main="HH", xlab="Wealth", ylab="Number of Extorters")
png(file=paste(imagePath,"/distributionExtorterWealth-Histogram.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Extorters Wealth Distribution")
dev.off()

###
### BOXPLOT WEALTH DISTRIBUTION
###
png(file=paste(imagePath,"/distributionExtorterWealth-Boxplot.png", sep=""), width=800, height=600)
qplot(factor(type), wealth, data=e, geom = "boxplot",
      main = "Extorters Wealth Distribution")
dev.off()

###
### JITTER WEALTH DISTRIBUTION
###
png(file=paste(imagePath,"/distributionExtorterWealth-Jitter.png", sep=""), width=800, height=600)
qplot(factor(type), wealth, data=e, geom = "jitter",
      main = "Extorters Wealth Distribution", colour=factor(type))
dev.off()


###
### NUMBER OF TARGETS DISTRIBUTION
###
minNumTargetX <- floor(min(e[,numTargets]))
maxNumTargetX <- ceiling(max(e[,numTargets]))

minY <- 0
maxY <- 7

plotLL <- qplot(numTargets, data=e[which(type=="0")], geom = "histogram",
                xlim = c(minNumTargetX, maxNumTargetX), #ylim = c(minY,maxY),
                main="LL", xlab="Number of Targets", ylab="Number of Extorters")
plotLH <- qplot(numTargets, data=e[which(type=="1")], geom = "histogram",
                xlim = c(minNumTargetX, maxNumTargetX), #ylim = c(minY,maxY),
                main="LH", xlab="Number of Targets", ylab="Number of Extorters")
plotHL <- qplot(numTargets, data=e[which(type=="2")], geom = "histogram",
                xlim = c(minNumTargetX, maxNumTargetX), #ylim = c(minY,maxY),
                main="HL", xlab="Number of Targets", ylab="Number of Extorters")
plotHH <- qplot(numTargets, data=e[which(type=="3")], geom = "histogram",
                xlim = c(minNumTargetX, maxNumTargetX), #ylim = c(minY,maxY),
                main="HH", xlab="Number of Targets", ylab="Number of Extorters")
png(file=paste(imagePath,"/distributionExtorterNumberTargets-Histogram.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Number of Targets Distribution")
dev.off()

###
### BOXPLOT NUMBER OF TARGETS DISTRIBUTION
###
png(file=paste(imagePath,"/distributionExtorterNumberTargets-Boxplot.png", sep=""), width=800, height=600)
qplot(factor(type), numTargets, data=e, geom = "boxplot",
      main = "Extorters Number of Targets Distribution")
dev.off()

###
### JITTER NUMBER OF TARGETS DISTRIBUTION
###
png(file=paste(imagePath,"/distributionExtorterNumberTargets-Jitter.png", sep=""), width=800, height=600)
qplot(factor(type), numTargets, data=e, geom = "jitter",
      main = "Extorters Number of Targets Distribution", colour=factor(type))
dev.off()


###
### TARGETS WEALTH DISTRIBUTION
###
png(file=paste(imagePath,"/distributionTargetsWealth-Histogram.png", sep=""), width=800, height=600)
qplot(wealth, data=t, geom = "histogram",
      main="Number of Targets Distribution", xlab="Wealth", ylab="Number of Targets")
dev.off()

###
### BOXPLOT TARGETS WEALTH DISTRIBUTION
###
png(file=paste(imagePath,"/distributionTargetsWealth-Boxplot.png", sep=""), width=800, height=600)
qplot(factor(type), wealth, data=t, geom = "boxplot",
      main = "Targets Wealth Distribution")
dev.off()

###
### JITTER TARGETS WEALTH DISTRIBUTION
###
png(file=paste(imagePath,"/distributionTargetsWealth-Jitter.png", sep=""), width=800, height=600)
qplot(factor(type), wealth, data=t, geom = "jitter", main = "Targets Wealth Distribution")
dev.off()