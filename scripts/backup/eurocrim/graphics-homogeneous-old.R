###
### Required libraries
###
library(data.table)
library(ggplot2)
library(gridExtra)

###
### Raw simulation data output directory
###
outputPath <- "/data/workspace/repast/intERS/output/28-enlarge-20-counterattack-20/homogeneous"

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

osLL <- data.table(read.csv(paste(outputPath,"/cSumObserversLL.csv",sep=""),sep=";"))
setkey(osLL, cycle)
osLH <- data.table(read.csv(paste(outputPath,"/cSumObserversLH.csv",sep=""),sep=";"))
setkey(osLH, cycle)
osHL <- data.table(read.csv(paste(outputPath,"/cSumObserversHL.csv",sep=""),sep=";"))
setkey(osHL, cycle)
osHH <- data.table(read.csv(paste(outputPath,"/cSumObserversHH.csv",sep=""),sep=";"))
setkey(osHH, cycle)

omLL <- data.table(read.csv(paste(outputPath,"/cAvgObserversLL.csv",sep=""),sep=";"))
setkey(omLL, cycle)
omLH <- data.table(read.csv(paste(outputPath,"/cAvgObserversLH.csv",sep=""),sep=";"))
setkey(omLH, cycle)
omHL <- data.table(read.csv(paste(outputPath,"/cAvgObserversHL.csv",sep=""),sep=";"))
setkey(omHL, cycle)
omHH <- data.table(read.csv(paste(outputPath,"/cAvgObserversHH.csv",sep=""),sep=";"))
setkey(omHH, cycle)

tsLL <- data.table(read.csv(paste(outputPath,"/cSumTargetsLL.csv",sep=""),sep=";"))
setkey(tsLL, cycle)
tsLH <- data.table(read.csv(paste(outputPath,"/cSumTargetsLH.csv",sep=""),sep=";"))
setkey(tsLH, cycle)
tsHL <- data.table(read.csv(paste(outputPath,"/cSumTargetsHL.csv",sep=""),sep=";"))
setkey(tsHL, cycle)
tsHH <- data.table(read.csv(paste(outputPath,"/cSumTargetsHH.csv",sep=""),sep=";"))
setkey(tsHH, cycle)

tmLL <- data.table(read.csv(paste(outputPath,"/cAvgTargetsLL.csv",sep=""),sep=";"))
setkey(tmLL, cycle)
tmLH <- data.table(read.csv(paste(outputPath,"/cAvgTargetsLH.csv",sep=""),sep=";"))
setkey(tmLH, cycle)
tmHL <- data.table(read.csv(paste(outputPath,"/cAvgTargetsHL.csv",sep=""),sep=";"))
setkey(tmHL, cycle)
tmHH <- data.table(read.csv(paste(outputPath,"/cAvgTargetsHH.csv",sep=""),sep=";"))
setkey(tmHH, cycle)


###
### NUMBER OF TARGETS
###
oLL <- osLL[, lapply(.SD,mean), by=cycle]
oLH <- osLH[, lapply(.SD,mean), by=cycle]
oHL <- osHL[, lapply(.SD,mean), by=cycle]
oHH <- osHH[, lapply(.SD,mean), by=cycle]

minTotalTargetsY <- floor(min(min(oLL[,totalTargets],oHH[,totalTargets]),
                              min(oLH[,totalTargets],oHL[,totalTargets])))
maxTotalTargetsY <- ceiling(max(max(oLL[,totalTargets],oHH[,totalTargets]),
                                max(oLH[,totalTargets],oHL[,totalTargets])))

plotLL <- qplot(cycle, totalTargets, data=oLL, geom = "line", ylim = c(minTotalTargetsY,maxTotalTargetsY),
                main="LL", xlab="", ylab="Number")
plotLH <- qplot(cycle, totalTargets, data=oLH, geom = "line", ylim = c(minTotalTargetsY,maxTotalTargetsY),
                main="LH", xlab="", ylab="Number")
plotHL <- qplot(cycle, totalTargets, data=oHL, geom = "line", ylim = c(minTotalTargetsY,maxTotalTargetsY),
                main="HL", xlab="", ylab="Number")
plotHH <- qplot(cycle, totalTargets, data=oHH, geom = "line", ylim = c(minTotalTargetsY,maxTotalTargetsY),
                main="HH", xlab="", ylab="Number")
png(file=paste(imagePath,"/numberTargets.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Number of Targets")
dev.off()


###
### NUMBER OF TARGETS MINIMUM EQUAL 0
###
oLL <- osLL[, lapply(.SD,mean), by=cycle]
oLH <- osLH[, lapply(.SD,mean), by=cycle]
oHL <- osHL[, lapply(.SD,mean), by=cycle]
oHH <- osHH[, lapply(.SD,mean), by=cycle]

minTotalTargetsY <- 0 # floor(min(min(oLL[,totalTargets],oHH[,totalTargets]),
#        min(oLH[,totalTargets],oHL[,totalTargets])))
maxTotalTargetsY <- ceiling(max(max(oLL[,totalTargets],oHH[,totalTargets]),
                                max(oLH[,totalTargets],oHL[,totalTargets])))

plotLL <- qplot(cycle, totalTargets, data=oLL, geom = "line", ylim = c(minTotalTargetsY,maxTotalTargetsY),
                main="LL", xlab="", ylab="Number")
plotLH <- qplot(cycle, totalTargets, data=oLH, geom = "line", ylim = c(minTotalTargetsY,maxTotalTargetsY),
                main="LH", xlab="", ylab="Number")
plotHL <- qplot(cycle, totalTargets, data=oHL, geom = "line", ylim = c(minTotalTargetsY,maxTotalTargetsY),
                main="HL", xlab="", ylab="Number")
plotHH <- qplot(cycle, totalTargets, data=oHH, geom = "line", ylim = c(minTotalTargetsY,maxTotalTargetsY),
                main="HH", xlab="", ylab="Number")
png(file=paste(imagePath,"/numberTargetsMin0.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Number of Targets")
dev.off()


###
### TARGETS ACCUMULATED WEALTH
###
tLL <- tsLL[, lapply(.SD,mean), by=cycle]
tLH <- tsLH[, lapply(.SD,mean), by=cycle]
tHL <- tsHL[, lapply(.SD,mean), by=cycle]
tHH <- tsHH[, lapply(.SD,mean), by=cycle]

minWealthY <- floor(min(min(tLL[,wealth],tHH[,wealth]),min(tLH[,wealth],tHL[,wealth])))
maxWealthY <- ceiling(max(max(tLL[,wealth],tHH[,wealth]),max(tLH[,wealth],tHL[,wealth])))

plotLL <- qplot(cycle, wealth, data=tLL, geom = "line", ylim = c(minWealthY,maxWealthY),
                main="LL", xlab="", ylab="Wealth")
plotLH <- qplot(cycle, wealth, data=tLH, geom = "line", ylim = c(minWealthY,maxWealthY),
                main="LH", xlab="", ylab="Wealth")
plotHL <- qplot(cycle, wealth, data=tHL, geom = "line", ylim = c(minWealthY,maxWealthY),
                main="HL", xlab="", ylab="Wealth")
plotHH <- qplot(cycle, wealth, data=tHH, geom = "line", ylim = c(minWealthY,maxWealthY),
                main="HH", xlab="", ylab="Wealth")
png(file=paste(imagePath,"/accumulatedTargetsWealth.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Targets Accumulated Wealth")
dev.off()


###
### ALL TARGETS AVERAGE INCOME
###
oLL <- osLL[, lapply(.SD,mean), by=cycle]
maxTotalTargetsLL <- max(oLL[,totalTargets])
oLH <- osLH[, lapply(.SD,mean), by=cycle]
maxTotalTargetsLH <- max(oLH[,totalTargets])
oHL <- osHL[, lapply(.SD,mean), by=cycle]
maxTotalTargetsHL <- max(oHL[,totalTargets])
oHH <- osHH[, lapply(.SD,mean), by=cycle]
maxTotalTargetsHH <- max(oHH[,totalTargets])

tLL <- tsLL[, lapply(.SD,mean), by=cycle]
tLH <- tsLH[, lapply(.SD,mean), by=cycle]
tHL <- tsHL[, lapply(.SD,mean), by=cycle]
tHH <- tsHH[, lapply(.SD,mean), by=cycle]

minIncomeY <- floor(min(min(tLL[,income]/maxTotalTargetsLL,tHH[,income]/maxTotalTargetsHH),
                        min(tLH[,income]/maxTotalTargetsLH,tHL[,income]/maxTotalTargetsHL)))
maxIncomeY <- ceiling(max(max(tLL[,income]/maxTotalTargetsLL,tHH[,income]/maxTotalTargetsHH),
                          max(tLH[,income]/maxTotalTargetsLH,tHL[,income]/maxTotalTargetsHL)))

plotLL <- qplot(cycle, income/maxTotalTargetsLL, data=tLL, geom = "line", ylim = c(minIncomeY,maxIncomeY),
                main="LL", xlab="", ylab="Income")
plotLH <- qplot(cycle, income/maxTotalTargetsLH, data=tLH, geom = "line", ylim = c(minIncomeY,maxIncomeY),
                main="LH", xlab="", ylab="Income")
plotHL <- qplot(cycle, income/maxTotalTargetsHL, data=tHL, geom = "line", ylim = c(minIncomeY,maxIncomeY),
                main="HL", xlab="", ylab="Income")
plotHH <- qplot(cycle, income/maxTotalTargetsHH, data=tHH, geom = "line", ylim = c(minIncomeY,maxIncomeY),
                main="HH", xlab="", ylab="Income")
png(file=paste(imagePath,"/averageAllTargetsIncome.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="All Targets Average Income")
dev.off()


###
### AVERAGE TARGETS INCOME PERCENTAGE PAID ON EXTORTION
###
tLL <- tsLL[, lapply(.SD,mean), by=cycle]
tLH <- tsLH[, lapply(.SD,mean), by=cycle]
tHL <- tsHL[, lapply(.SD,mean), by=cycle]
tHH <- tsHH[, lapply(.SD,mean), by=cycle]

minIncomeY <- floor(min(min(tLL[,totalPaid]/tLL[,income+totalPaid+totalPunishment],
                            tHH[,totalPaid]/tHH[,income+totalPaid+totalPunishment]),
                        min(tLH[,totalPaid]/tLH[,income+totalPaid+totalPunishment],
                            tHL[,totalPaid]/tHL[,income+totalPaid+totalPunishment])))
maxIncomeY <- ceiling(max(max(tLL[,totalPaid]/tLL[,income+totalPaid+totalPunishment],
                              tHH[,totalPaid]/tHH[,income+totalPaid+totalPunishment]),
                          max(tLH[,totalPaid]/tLH[,income+totalPaid+totalPunishment],
                              tHL[,totalPaid]/tHL[,income+totalPaid+totalPunishment])))

plotLL <- qplot(cycle, totalPaid/(income+totalPaid+totalPunishment), data=tLL, geom = "line",
                ylim = c(minIncomeY,maxIncomeY), main="LL", xlab="", ylab="Percentage")
plotLH <- qplot(cycle, totalPaid/(income+totalPaid+totalPunishment), data=tLH, geom = "line",
                ylim = c(minIncomeY,maxIncomeY), main="LH", xlab="", ylab="Percentage")
plotHL <- qplot(cycle, totalPaid/(income+totalPaid+totalPunishment), data=tHL, geom = "line",
                ylim = c(minIncomeY,maxIncomeY), main="HL", xlab="", ylab="Percentage")
plotHH <- qplot(cycle, totalPaid/(income+totalPaid+totalPunishment), data=tHH, geom = "line",
                ylim = c(minIncomeY,maxIncomeY), main="HH", xlab="", ylab="Percentage")
png(file=paste(imagePath,"/averageAllTargetsExtortedIncome.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Average Targets Income Percentage Paid on Extortion")
dev.off()


###
### NUMBER OF EXTORTERS
###
oLL <- osLL[, lapply(.SD,mean), by=cycle]
oLH <- osLH[, lapply(.SD,mean), by=cycle]
oHL <- osHL[, lapply(.SD,mean), by=cycle]
oHH <- osHH[, lapply(.SD,mean), by=cycle]

minTotalExtortersY <- 0 # floor(min(min(oLL[,totalExtortersFree+totalExtortersImprisoned],
#            oHH[,totalExtortersFree+totalExtortersImprisoned]),
#        min(oLH[,totalExtortersFree+totalExtortersImprisoned],
#            oHL[,totalExtortersFree+totalExtortersImprisoned])))
maxTotalExtortersY <- ceiling(max(max(oLL[,totalExtortersFree+totalExtortersImprisoned],
                                      oHH[,totalExtortersFree+totalExtortersImprisoned]),
                                  max(oLH[,totalExtortersFree+totalExtortersImprisoned],
                                      oHL[,totalExtortersFree+totalExtortersImprisoned])))

plotLL <- qplot(cycle, totalExtortersFree+totalExtortersImprisoned, data=oLL, geom = "line",
                ylim = c(minTotalExtortersY,maxTotalExtortersY),
                main="LL", xlab="", ylab="Number")
plotLH <- qplot(cycle, totalExtortersFree+totalExtortersImprisoned, data=oLH, geom = "line",
                ylim = c(minTotalExtortersY,maxTotalExtortersY),
                main="LH", xlab="", ylab="Number")
plotHL <- qplot(cycle, totalExtortersFree+totalExtortersImprisoned, data=oHL, geom = "line",
                ylim = c(minTotalExtortersY,maxTotalExtortersY),
                main="HL", xlab="", ylab="Number")
plotHH <- qplot(cycle, totalExtortersFree+totalExtortersImprisoned, data=oHH, geom = "line",
                ylim = c(minTotalExtortersY,maxTotalExtortersY),
                main="HH", xlab="", ylab="Number")
png(file=paste(imagePath,"/numberExtorters.png", sep=""), width=800, height=600)
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
oLL <- osLL[, lapply(.SD,mean), by=cycle]
maxTotalExtortersLL <- max(oLL[,totalExtortersFree+totalExtortersImprisoned])
oLH <- osLH[, lapply(.SD,mean), by=cycle]
maxTotalExtortersLH <- max(oLH[,totalExtortersFree+totalExtortersImprisoned])
oHL <- osHL[, lapply(.SD,mean), by=cycle]
maxTotalExtortersHL <- max(oHL[,totalExtortersFree+totalExtortersImprisoned])
oHH <- osHH[, lapply(.SD,mean), by=cycle]
maxTotalExtortersHH <- max(oHH[,totalExtortersFree+totalExtortersImprisoned])

eLL <- emLL[, lapply(.SD,mean), by=cycle]
eLH <- emLH[, lapply(.SD,mean), by=cycle]
eHL <- emHL[, lapply(.SD,mean), by=cycle]
eHH <- emHH[, lapply(.SD,mean), by=cycle]

minWealthY <- floor(min(min(eLL[,wealth]/maxTotalExtortersLL,eHH[,wealth]/maxTotalExtortersHH),
                        min(eLH[,wealth]/maxTotalExtortersLH,eHL[,wealth]/maxTotalExtortersHL)))
maxWealthY <- ceiling(max(max(eLL[,wealth]/maxTotalExtortersLL,eHH[,wealth]/maxTotalExtortersHH),
                          max(eLH[,wealth]/maxTotalExtortersLH,eHL[,wealth]/maxTotalExtortersHL)))

plotLL <- qplot(cycle, wealth/maxTotalExtortersLL, data=eLL, geom = "line", ylim = c(minWealthY,maxWealthY),
                main="LL", xlab="", ylab="Wealth")
plotLH <- qplot(cycle, wealth/maxTotalExtortersLH, data=eLH, geom = "line", ylim = c(minWealthY,maxWealthY),
                main="LH", xlab="", ylab="Wealth")
plotHL <- qplot(cycle, wealth/maxTotalExtortersHL, data=eHL, geom = "line", ylim = c(minWealthY,maxWealthY),
                main="HL", xlab="", ylab="Wealth")
plotHH <- qplot(cycle, wealth/maxTotalExtortersHH, data=eHH, geom = "line", ylim = c(minWealthY,maxWealthY),
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
oLL <- osLL[, lapply(.SD,mean), by=cycle]
maxTotalTargetsLL <- max(oLL[,totalTargets])
oLH <- osLH[, lapply(.SD,mean), by=cycle]
maxTotalTargetsLH <- max(oLH[,totalTargets])
oHL <- osHL[, lapply(.SD,mean), by=cycle]
maxTotalTargetsHL <- max(oHL[,totalTargets])
oHH <- osHH[, lapply(.SD,mean), by=cycle]
maxTotalTargetsHH <- max(oHH[,totalTargets])

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
tLL <- tsLL[, lapply(.SD,mean), by=cycle]
tLH <- tsLH[, lapply(.SD,mean), by=cycle]
tHL <- tsHL[, lapply(.SD,mean), by=cycle]
tHH <- tsHH[, lapply(.SD,mean), by=cycle]

minNumPaidY <- 0 # floor(min(min(tLL[,numPaid],tHH[,numPaid]),min(tLH[,numPaid],tHL[,numPaid])))
maxNumPaidY <- ceiling(max(max(tLL[,numPaid],tHH[,numPaid]),max(tLH[,numPaid],tHL[,numPaid])))

plotLL <- qplot(cycle, numPaid, data=tLL, geom = "line", ylim = c(minNumPaidY,maxNumPaidY),
                main="LL", xlab="", ylab="Number")
plotLH <- qplot(cycle, numPaid, data=tLH, geom = "line", ylim = c(minNumPaidY,maxNumPaidY),
                main="LH", xlab="", ylab="Number")
plotHL <- qplot(cycle, numPaid, data=tHL, geom = "line", ylim = c(minNumPaidY,maxNumPaidY),
                main="HL", xlab="", ylab="Number")
plotHH <- qplot(cycle, numPaid, data=tHH, geom = "line", ylim = c(minNumPaidY,maxNumPaidY),
                main="HH", xlab="", ylab="Number")
png(file=paste(imagePath,"/numberExtortionsPaid.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Extortions Paid")
dev.off()


###
### NUMBER OF PAID EXTORTIONS 1:250
###
tLL <- tsLL[, lapply(.SD,mean), by=cycle]
tLH <- tsLH[, lapply(.SD,mean), by=cycle]
tHL <- tsHL[, lapply(.SD,mean), by=cycle]
tHH <- tsHH[, lapply(.SD,mean), by=cycle]

minNumPaidY <- 0 # floor(min(min(tLL[1:250,numPaid],tHH[1:250,numPaid]),
#         min(tLH[1:250,numPaid],tHL[1:250,numPaid])))
maxNumPaidY <- ceiling(max(max(tLL[1:250,numPaid],tHH[1:250,numPaid]),
                           max(tLH[1:250,numPaid],tHL[1:250,numPaid])))

plotLL <- qplot(cycle, numPaid, data=tLL[1:250,], geom = "line", ylim = c(minNumPaidY,maxNumPaidY),
                main="LL", xlab="", ylab="Number")
plotLH <- qplot(cycle, numPaid, data=tLH[1:250,], geom = "line", ylim = c(minNumPaidY,maxNumPaidY),
                main="LH", xlab="", ylab="Number")
plotHL <- qplot(cycle, numPaid, data=tHL[1:250,], geom = "line", ylim = c(minNumPaidY,maxNumPaidY),
                main="HL", xlab="", ylab="Number")
plotHH <- qplot(cycle, numPaid, data=tHH[1:250,], geom = "line", ylim = c(minNumPaidY,maxNumPaidY),
                main="HH", xlab="", ylab="Number")
png(file=paste(imagePath,"/numberExtortionsPaid-1:250.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Extortions Paid")
dev.off()


###
### NUMBER OF NON PAID EXTORTIONS
###
tLL <- tsLL[, lapply(.SD,mean), by=cycle]
tLH <- tsLH[, lapply(.SD,mean), by=cycle]
tHL <- tsHL[, lapply(.SD,mean), by=cycle]
tHH <- tsHH[, lapply(.SD,mean), by=cycle]

minNumNotPaidY <- 0 # floor(min(min(tLL[,numNotPaid],tHH[,numNotPaid]),min(tLH[,numNotPaid],tHL[,numNotPaid])))
maxNumNotPaidY <- ceiling(max(max(tLL[,numNotPaid],tHH[,numNotPaid]),max(tLH[,numNotPaid],tHL[,numNotPaid])))

plotLL <- qplot(cycle, numNotPaid, data=tLL, geom = "line", ylim = c(minNumNotPaidY,maxNumNotPaidY),
                main="LL", xlab="", ylab="Number")
plotLH <- qplot(cycle, numNotPaid, data=tLH, geom = "line", ylim = c(minNumNotPaidY,maxNumNotPaidY),
                main="LH", xlab="", ylab="Number")
plotHL <- qplot(cycle, numNotPaid, data=tHL, geom = "line", ylim = c(minNumNotPaidY,maxNumNotPaidY),
                main="HL", xlab="", ylab="Number")
plotHH <- qplot(cycle, numNotPaid, data=tHH, geom = "line", ylim = c(minNumNotPaidY,maxNumNotPaidY),
                main="HH", xlab="", ylab="Number")
png(file=paste(imagePath,"/numberExtortionsNotPaid.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Extortions Not Paid")
dev.off()


###
### NUMBER OF NON PAID EXTORTIONS 1:250
###
tLL <- tsLL[, lapply(.SD,mean), by=cycle]
tLH <- tsLH[, lapply(.SD,mean), by=cycle]
tHL <- tsHL[, lapply(.SD,mean), by=cycle]
tHH <- tsHH[, lapply(.SD,mean), by=cycle]

minNumNotPaidY <- 0 # floor(min(min(tLL[1:250,numNotPaid],tHH[1:250,numNotPaid]),
#        min(tLH[1:250,numNotPaid],tHL[1:250,numNotPaid])))
maxNumNotPaidY <- ceiling(max(max(tLL[1:250,numNotPaid],tHH[1:250,numNotPaid]),
                              max(tLH[1:250,numNotPaid],tHL[1:250,numNotPaid])))

plotLL <- qplot(cycle, numNotPaid, data=tLL[1:250,], geom = "line", ylim = c(minNumNotPaidY,maxNumNotPaidY),
                main="LL", xlab="", ylab="Number")
plotLH <- qplot(cycle, numNotPaid, data=tLH[1:250,], geom = "line", ylim = c(minNumNotPaidY,maxNumNotPaidY),
                main="LH", xlab="", ylab="Number")
plotHL <- qplot(cycle, numNotPaid, data=tHL[1:250,], geom = "line", ylim = c(minNumNotPaidY,maxNumNotPaidY),
                main="HL", xlab="", ylab="Number")
plotHH <- qplot(cycle, numNotPaid, data=tHH[1:250,], geom = "line", ylim = c(minNumNotPaidY,maxNumNotPaidY),
                main="HH", xlab="", ylab="Number")
png(file=paste(imagePath,"/numberExtortionsNotPaid-1:250.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Extortions Not Paid")
dev.off()


###
### SOCIETY WEALTH LOSSES AND PERCENTAGE
###
tLL <- tsLL[, lapply(.SD,mean), by=cycle]
tLH <- tsLH[, lapply(.SD,mean), by=cycle]
tHL <- tsHL[, lapply(.SD,mean), by=cycle]
tHH <- tsHH[, lapply(.SD,mean), by=cycle]

eLL <- esLL[, lapply(.SD,mean), by=cycle]
eLH <- esLH[, lapply(.SD,mean), by=cycle]
eHL <- esHL[, lapply(.SD,mean), by=cycle]
eHH <- esHH[, lapply(.SD,mean), by=cycle]

cycleLL <- seq(1:nrow(tLL))
dataLLWealth <- mapply(sum, tLL[,wealth], eLL[,wealth])
dataLLLosses <- mapply(sum, tLL[,totalPunishment], eLL[,totalLostFight])
dataLL <- cbind(cycleLL, dataLLWealth, dataLLLosses)
dataLL <- data.table(dataLL)
setnames(dataLL, c("dataLL","wealth","losses"))

cycleLH <- seq(1:nrow(tLH))
dataLHWealth <- mapply(sum, tLH[,wealth], eLH[,wealth])
dataLHLosses <- mapply(sum, tLH[,totalPunishment], eLH[,totalLostFight])
dataLH <- cbind(cycleLH, dataLHWealth, dataLHLosses)
dataLH <- data.table(dataLH)
setnames(dataLH, c("dataLH","wealth","losses"))

cycleHL <- seq(1:nrow(tHL))
dataHLWealth <- mapply(sum, tHL[,wealth], eHL[,wealth])
dataHLLosses <- mapply(sum, tHL[,totalPunishment], eHL[,totalLostFight])
dataHL <- cbind(cycleHL, dataHLWealth, dataHLLosses)
dataHL <- data.table(dataHL)
setnames(dataHL, c("dataHL","wealth","losses"))

cycleHH <- seq(1:nrow(tHH))
dataHHWealth <- mapply(sum, tHH[,wealth], eHH[,wealth])
dataHHLosses <- mapply(sum, tHH[,totalPunishment], eHH[,totalLostFight])
dataHH <- cbind(cycleHH, dataHHWealth, dataHHLosses)
dataHH <- data.table(dataHH)
setnames(dataHH, c("dataHH","wealth","losses"))

minWealthY <- floor(min(min(dataLL[,wealth],dataHH[,wealth]),
                        min(dataLH[,wealth],dataHL[,wealth])))
maxWealthY <- ceiling(max(max(dataLL[,wealth],dataHH[,wealth]),
                          max(dataLH[,wealth],dataHL[,wealth])))

plotLL <- qplot(cycleLL, wealth, data=dataLL, geom = "line", ylim = c(minWealthY,maxWealthY),
                main="LL", xlab="", ylab="Wealth")
plotLH <- qplot(cycleLH, wealth, data=dataLH, geom = "line", ylim = c(minWealthY,maxWealthY),
                main="LH", xlab="", ylab="Wealth")
plotHL <- qplot(cycleHL, wealth, data=dataHL, geom = "line", ylim = c(minWealthY,maxWealthY),
                main="HL", xlab="", ylab="Wealth")
plotHH <- qplot(cycleHH, wealth, data=dataHH, geom = "line", ylim = c(minWealthY,maxWealthY),
                main="HH", xlab="", ylab="Wealth")
png(file=paste(imagePath,"/societyWealth.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Society Wealth")
dev.off()

minLossesY <- floor(min(min(dataLL[,losses],dataHH[,losses]),
                        min(dataLH[,losses],dataHL[,losses])))
maxLossesY <- ceiling(max(max(dataLL[,losses],dataHH[,losses]),
                          max(dataLH[,losses],dataHL[,losses])))

plotLL <- qplot(cycleLL, losses, data=dataLL, geom = "line", ylim = c(minLossesY,maxLossesY),
                main="LL", xlab="", ylab="Wealth")
plotLH <- qplot(cycleLH, losses, data=dataLH, geom = "line", ylim = c(minLossesY,maxLossesY),
                main="LH", xlab="", ylab="Wealth")
plotHL <- qplot(cycleHL, losses, data=dataHL, geom = "line", ylim = c(minLossesY,maxLossesY),
                main="HL", xlab="", ylab="Wealth")
plotHH <- qplot(cycleHH, losses, data=dataHH, geom = "line", ylim = c(minLossesY,maxLossesY),
                main="HH", xlab="", ylab="Wealth")
png(file=paste(imagePath,"/societyLosses.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Society Losses")
dev.off()


minY <- floor(min(min(dataLL[,losses/(wealth+losses)],dataHH[,losses/(wealth+losses)]),
                  min(dataLH[,losses/(wealth+losses)],dataHL[,losses/(wealth+losses)])))
maxY <- ceiling(max(max(dataLL[,losses/(wealth+losses)],dataHH[,losses/(wealth+losses)]),
                    max(dataLH[,losses/(wealth+losses)],dataHL[,losses/(wealth+losses)])))

plotLL <- qplot(cycleLL, losses/(wealth+losses), data=dataLL, geom = "line", ylim = c(minY,maxY),
                main="LL", xlab="", ylab="Percentage")
plotLH <- qplot(cycleLH, losses/(wealth+losses), data=dataLH, geom = "line", ylim = c(minY,maxY),
                main="LH", xlab="", ylab="Percentage")
plotHL <- qplot(cycleHL, losses/(wealth+losses), data=dataHL, geom = "line", ylim = c(minY,maxY),
                main="HL", xlab="", ylab="Percentage")
plotHH <- qplot(cycleHH, losses/(wealth+losses), data=dataHH, geom = "line", ylim = c(minY,maxY),
                main="HH", xlab="", ylab="Percentage")
png(file=paste(imagePath,"/societyLossesPercentage.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Society Losses Percentage")
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
maxY <- 35

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
minNumTargetsX <- floor(min(e[,numTargets]))
maxNumTargetsX <- ceiling(max(e[,numTargets]))

minY <- 0
maxY <- 30

plotLL <- qplot(numTargets, data=e[which(type=="0")], geom = "histogram",
                xlim = c(minNumTargetsX, maxNumTargetsX), #ylim = c(minY,maxY),
                main="LL", xlab="Number of Targets", ylab="Number of Extorters")
plotLH <- qplot(numTargets, data=e[which(type=="1")], geom = "histogram",
                xlim = c(minNumTargetsX, maxNumTargetsX), #ylim = c(minY,maxY),
                main="LH", xlab="Number of Targets", ylab="Number of Extorters")
plotHL <- qplot(numTargets, data=e[which(type=="2")], geom = "histogram",
                xlim = c(minNumTargetsX, maxNumTargetsX), #ylim = c(minY,maxY),
                main="HL", xlab="Number of Targets", ylab="Number of Extorters")
plotHH <- qplot(numTargets, data=e[which(type=="3")], geom = "histogram",
                xlim = c(minNumTargetsX, maxNumTargetsX), #ylim = c(minY,maxY),
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
minWealthX <- floor(min(t[,wealth]))
maxWealthX <- ceiling(max(t[,wealth]))

minY <- 0
maxY <- 300

plotLL <- qplot(wealth, data=t[which(type=="0")], geom = "histogram",
                xlim = c(minWealthX, maxWealthX), #ylim = c(minY,maxY),
                main="LL", xlab="Wealth", ylab="Number of Targets")
plotLH <- qplot(wealth, data=t[which(type=="1")], geom = "histogram",
                xlim = c(minWealthX, maxWealthX), #ylim = c(minY,maxY),
                main="LH", xlab="Wealth", ylab="Number of Targets")
plotHL <- qplot(wealth, data=t[which(type=="2")], geom = "histogram",
                xlim = c(minWealthX, maxWealthX), #ylim = c(minY,maxY),
                main="HL", xlab="Wealth", ylab="Number of Targets")
plotHH <- qplot(wealth, data=t[which(type=="3")], geom = "histogram",
                xlim = c(minWealthX, maxWealthX), #ylim = c(minY,maxY),
                main="HH", xlab="Wealth", ylab="Number of Targets")
png(file=paste(imagePath,"/distributionTargetsWealth-Histogram.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Number of Targets Distribution")
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
qplot(factor(type), wealth, data=t, geom = "jitter",
      main = "Targets Wealth Distribution", colour=factor(type))
dev.off()