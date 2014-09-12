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
v <- as.numeric(args[1])
i <- as.numeric(args[2])

###
### Raw simulation data output directory
###
basePath <- paste("/data/workspace/gloders/intERS/output/v2/",sep="")
dir.create(file.path(basePath, "imagesSingleCOLOR"), showWarnings = FALSE)
imagePath <- paste(basePath,"imagesSingleCOLOR", sep="")

###
### Constants
###
NO_COMPETITION <- 1
NO_PROTECTION <- 2
PROTECTION <- 3
  
RANGE <- 1:600
  
CYCLES <- 300
SIMULATIONS <- 5
EXTORTERS <- 20
  
###
### Global Variables
###
extorterSum <- list()
extorterAvg <- list()
observerSum <- list()
observerAvg <- list()
targetSum <- list()
targetAvg <- list()
monopolyCycle <- list()
#imagePath <- list()


###
### Output path
###
outputPath <- list()
outputPath[[NO_COMPETITION]] <- paste(basePath, "noCompetition", sep="")
outputPath[[NO_PROTECTION]] <- paste(basePath, "noProtection", sep="")
outputPath[[PROTECTION]] <- paste(basePath, "protection", sep="")
  
for(i in 1:3) {
  ###
  ### Upload Consolidated Information
  ###
  extorterSum[[i]] <- data.table(
    read.csv(paste(outputPath[[i]],"/cSumextorters.csv",sep=""),sep=";"))
  setkey(extorterSum[[i]], type, cycle)
  
  extorterAvg[[i]] <- data.table(
    read.csv(paste(outputPath[[i]],"/cAvgextorters.csv",sep=""),sep=";"))
  setkey(extorterAvg[[i]], type, cycle)
  
  observerSum[[i]] <- data.table(
    read.csv(paste(outputPath[[i]],"/","cSumobservers.csv",sep=""), sep=";"))
  setkey(observerSum[[i]], type, cycle)

  observerAvg[[i]] <- data.table(
    read.csv(paste(outputPath[[i]],"/","cAvgobservers.csv",sep=""), sep=";"))
  setkey(observerAvg[[i]], type, cycle)

  targetSum[[i]] <- data.table(
    read.csv(paste(outputPath[[i]],"/","cSumtargets.csv",sep=""), sep=";"))
  setkey(targetSum[[i]], type, cycle)

  targetAvg[[i]] <- data.table(
    read.csv(paste(outputPath[[i]],"/","cAvgtargets.csv",sep=""), sep=";"))
  setkey(targetAvg[[i]], type, cycle)


  ###
  ### GLOBAL VARIABLES
  ###
  monopolyCycle[[i]] <- min(observerSum[[i]][which((totalFR+totalIM) <= 1),cycle],
                            nrow(observerSum[[i]])+1)
  
  if (i == 1) {
    for(x in (monopolyCycle[[i]] + 1):max(RANGE)) {
      observerAvg[[i]][x, extortion := 0]
      observerAvg[[i]][x, punishment := 0]
      
      observerSum[[i]][x, totalTS := 0]
      observerSum[[i]][x, totalFR := 0]
      observerSum[[i]][x, totalIM := 0]
      
      targetSum[[i]][x, numExtortion := 0]
      targetSum[[i]][x, totalExtortionPaid := 0]
      targetSum[[i]][x, numExtortionPaid := 0]
      targetSum[[i]][x, totalPunishment := 0]
      targetSum[[i]][x, numPunishment := 0]
      
      extorterSum[[i]][x, CounterattackProtection := 0]
      extorterSum[[i]][x, CounterattackRetaliation := 0]
    }
  }
  
  if ((i == 2) | (i == 3)) {
    observerAvg[[i]] <- observerAvg[[i]][which(cycle <= monopolyCycle[[i]])]
    observerSum[[i]] <- observerSum[[i]][which(cycle <= monopolyCycle[[i]])]
    targetSum[[i]] <- targetSum[[i]][which(cycle <= monopolyCycle[[i]])]
    
    for(x in (monopolyCycle[[i]] + 1):max(RANGE)) {
      observerAvg[[i]] <- rbind(observerAvg[[i]], observerAvg[[i]][
        which(cycle == monopolyCycle[[i]]),])
      observerAvg[[i]][x + 1, cycle := as.integer(x)]
    
      observerSum[[i]] <- rbind(observerSum[[i]], observerSum[[i]][
        which(cycle == monopolyCycle[[i]]),])
      observerSum[[i]][x + 1, cycle := as.integer(x)]
      
      targetSum[[i]] <- rbind(targetSum[[i]], targetSum[[i]][
        which(cycle == monopolyCycle[[i]]),])
      targetSum[[i]][x + 1, cycle := as.integer(x)]
    }
  }
}

###
### SUMMARY (IT CAN BE SKIPPED)
###
eWealth <- matrix(0, nrow=3, ncol=SIMULATIONS)
tWealth <- matrix(0, nrow=3, ncol=SIMULATIONS)
nTargets <- matrix(0, nrow=3, ncol=SIMULATIONS)
nFights <- matrix(0, nrow=3, ncol=SIMULATIONS)
for(i in 2:3) {
  for(sim in 1:SIMULATIONS) {
    extorter <- data.table(
      read.csv(paste(outputPath[[i]],"/",sim,"/","extorters.csv",sep=""), sep=";",header=TRUE))
    eWealth[i,sim] <- extorter[which((cycle == monopolyCycle[3]) & (wealth > 0)),]$wealth
    nFights[i,sim] <- extorter[which((cycle == monopolyCycle[3]) & (wealth > 0)),]$numCounterattackProtection +
      extorter[which((cycle == monopolyCycle[3]) & (wealth > 0)),]$numCounterattackRetaliation
    
    target <- data.table(
      read.csv(paste(outputPath[[i]],"/",sim,"/","targets.csv",sep=""), sep=";",header=TRUE))
    tWealth[i,sim] <- sum(target[which((cycle == monopolyCycle[3]) & (wealth > 0)),]$wealth)
    nTargets[i,sim] <- length(target[which(cycle == monopolyCycle[3]),]$wealth)
  }
}


###
### AGGREGATE DATA
###
i <- NO_COMPETITION
range <- 1:monopolyCycle[[i]]
type <- rep(i,max(range))
NC <- cbind(type, observerSum[[i]][range,]$cycle,
            # Number of Extorters
            observerSum[[i]][range,]$totalFR+observerSum[[i]][range,]$totalIM,
            # Number of Targets
            observerSum[[i]][range,]$totalTA,
            # Proportion of target's income spent on paying extortion
            ifelse(targetSum[[i]][range,]$income != 0, (targetSum[[i]][range,]$totalExtortionPaid /
                                                          targetSum[[i]][range,]$income) * 100, 0),
            # Proportion of extortion demanded that resulted in punishment
            ifelse(targetSum[[i]][range,]$income != 0, (targetSum[[i]][range,]$numPunishment /
                                                          targetSum[[i]][range,]$numExtortion) * 100, 0),
            # Number of fights among extorters
            extorterSum[[i]][range,]$numCounterattackProtection +
              extorterSum[[i]][range,]$numCounterattackRetaliation,
            # Severity of punishment
            ifelse(targetSum[[i]][range,]$income != 0, (targetSum[[i]][range,]$totalPunishment /
                                                          targetSum[[i]][range,]$income) * 100, 0)
            )
NC <- data.table(NC)
setnames(NC,
         c("type", "V2", "V3", "V4", "V5", "V6", "V7", "V8"),
         c("type", "cycle", "numExtorters", "numTargets", "targetsIncome",
           "extortionPunished", "numFights", "sevPunishment"))


i <- NO_PROTECTION
range <- 1:monopolyCycle[[i]]
type <- rep(i,max(range))
CN <- cbind(type, observerSum[[i]][range,]$cycle,
            # Number of Extorters
            observerSum[[i]][range,]$totalFR+observerSum[[i]][range,]$totalIM,
            # Number of Targets
            observerSum[[i]][range,]$totalTA,
            # Proportion of target's income spent on paying extortion
            ifelse(targetSum[[i]][range,]$income != 0, (targetSum[[i]][range,]$totalExtortionPaid /
                                                          targetSum[[i]][range,]$income) * 100, 0),
            # Proportion of extortion demanded that resulted in punishment
            ifelse(targetSum[[i]][range,]$income != 0, (targetSum[[i]][range,]$numPunishment /
                                                          targetSum[[i]][range,]$numExtortion) * 100, 0),
            # Number of fights among extorters
            extorterSum[[i]][range,]$numCounterattackProtection +
              extorterSum[[i]][range,]$numCounterattackRetaliation,
              # Severity of punishment
            ifelse(targetSum[[i]][range,]$income != 0, (targetSum[[i]][range,]$totalPunishment /
                                                          targetSum[[i]][range,]$income) * 100, 0)
)
CN <- data.table(CN)
setnames(CN,
         c("type", "V2", "V3", "V4", "V5", "V6", "V7", "V8"),
         c("type", "cycle", "numExtorters", "numTargets", "targetsIncome",
           "extortionPunished", "numFights", "sevPunishment"))


i <- PROTECTION
range <- 1:monopolyCycle[[i]]
type <- rep(i,max(range))
CP <- cbind(type, observerSum[[i]][range,]$cycle,
            # Number of Extorters
            observerSum[[i]][range,]$totalFR+observerSum[[i]][range,]$totalIM,
            # Number of Targets
            observerSum[[i]][range,]$totalTA,
            # Proportion of target's income spent on paying extortion
            ifelse(targetSum[[i]][range,]$income != 0, (targetSum[[i]][range,]$totalExtortionPaid /
                                                          targetSum[[i]][range,]$income) * 100, 0),
            # Proportion of extortion demanded that resulted in punishment
            ifelse(targetSum[[i]][range,]$income != 0, (targetSum[[i]][range,]$numPunishment /
                                                          targetSum[[i]][range,]$numExtortion) * 100, 0),
            # Number of fights among extorters
            extorterSum[[i]][range,]$numCounterattackProtection +
              extorterSum[[i]][range,]$numCounterattackRetaliation,
            # Severity of punishment
            ifelse(targetSum[[i]][range,]$income != 0, (targetSum[[i]][range,]$totalPunishment /
                                                          targetSum[[i]][range,]$income) * 100, 0)
)
CP <- data.table(CP)
setnames(CP,
         c("type", "V2", "V3", "V4", "V5", "V6", "V7", "V8"),
         c("type", "cycle", "numExtorters", "numTargets", "targetsIncome",
           "extortionPunished", "numFights", "sevPunishment"))

minX <- 0
maxX <- max(NC$cycle,CN$cycle,CP$cycle)

##
## Non-Competition
##
data <- NC

##
## NUMBER OF EXTORTERS
##
png(file=paste(imagePath,"/NC-numberExtorters.png", sep=""),
    width=800, height=600)

minY <- 0
maxY <- ceiling(max(NC$numExtorters,CN$numExtorters,CP$numExtorters))

ggplot(data, aes(x = cycle, y = numExtorters,
                 color = factor(type),
                 shape = factor(type),
                 linetype = factor(type))) +
  scale_colour_manual(values=c("black"),
                      labels=c("No Competition")) +
  scale_shape_manual(values=c(17),
                     labels=c("No Competition")) +
  scale_linetype_manual(values=c(1),
                        labels=c("No Competition")) +
  geom_line(size = 1) +
  coord_cartesian(ylim=c(minY, maxY + 1), xlim=c(minX, maxX + 1)) +
  geom_vline(xintercept = monopolyCycle[[NO_COMPETITION]],
             linetype=2, size=1.5, color="black") +
  labs(x = "Round", y = "Number",
       colour = "Treatment",
       shape = "Treatment",
       linetype = "Treatment") +
  theme(axis.text = element_text(size = 16, face = "bold", colour = "black"),
        axis.title = element_text(size = 18, face = "bold"),
        legend.title = element_blank(),
        legend.text = element_text(size=16, face = "bold"),
        legend.position = "top")

dev.off()


##
## NUMBER OF TARGETS
##
png(file=paste(imagePath,"/NC-numberTargets.png", sep=""),
    width=800, height=600)

minY <- 0
maxY <- ceiling(max(NC$numTargets,CN$numTargets,CP$numTargets))

ggplot(data, aes(x = cycle, y = numTargets,
                 color = factor(type),
                 shape = factor(type),
                 linetype = factor(type))) +
  scale_colour_manual(values=c("black"),
                      labels=c("No Competition")) +
  scale_shape_manual(values=c(17),
                     labels=c("No Competition")) +
  scale_linetype_manual(values=c(1),
                        labels=c("No Competition")) +
  geom_line(size = 1) +
  coord_cartesian(ylim=c(minY, maxY + 1), xlim=c(minX, maxX + 1)) +
  geom_vline(xintercept = monopolyCycle[[NO_COMPETITION]],
             linetype=2, size=1.5, color="black") +
  labs(x = "Round", y = "Number",
       colour = "Treatment",
       shape = "Treatment",
       linetype = "Treatment") +
  theme(axis.text = element_text(size = 16, face = "bold", colour = "black"),
        axis.title = element_text(size = 18, face = "bold"),
        legend.title = element_blank(),
        legend.text = element_text(size=16, face = "bold"),
        legend.position = "top")

dev.off()


##
## TARGET'S INCOME SPENT ON PAYING EXTORTION
##
png(file=paste(imagePath,"/NC-targetsIncome.png", sep=""),
    width=800, height=600)

minY <- 0
maxY <- ceiling(max(NC$targetsIncome,CN$targetsIncome,CP$targetsIncome))

ggplot(data, aes(x = cycle, y = targetsIncome,
                 color = factor(type),
                 shape = factor(type),
                 linetype = factor(type))) +
  scale_colour_manual(values=c("black"),
                      labels=c("No Competition")) +
  scale_shape_manual(values=c(17),
                     labels=c("No Competition")) +
  scale_linetype_manual(values=c(1),
                        labels=c("No Competition")) +
  geom_line(size = 1) +
  coord_cartesian(ylim=c(minY, maxY + 1), xlim=c(minX, maxX + 1)) +
  geom_vline(xintercept = monopolyCycle[[NO_COMPETITION]],
             linetype=2, size=1.5, color="black") +
  labs(x = "Round", y = "%",
       colour = "Treatment",
       shape = "Treatment",
       linetype = "Treatment") +
  theme(axis.text = element_text(size = 16, face = "bold", colour = "black"),
        axis.title = element_text(size = 18, face = "bold"),
        legend.title = element_blank(),
        legend.text = element_text(size=16, face = "bold"),
        legend.position = "top")

dev.off()


##
## EXTORTIONS THAT RESULTED IN PUNISHMENT
##
png(file=paste(imagePath,"/NC-extortionPunished.png", sep=""),
    width=800, height=600)

minY <- 0
maxY <- ceiling(max(NC$extortionPunished,CN$extortionPunished,CP$extortionPunished))

ggplot(data, aes(x = cycle, y = extortionPunished,
                 color = factor(type),
                 shape = factor(type),
                 linetype = factor(type))) +
  scale_colour_manual(values=c("black"),
                      labels=c("No Competition")) +
  scale_shape_manual(values=c(17),
                     labels=c("No Competition")) +
  scale_linetype_manual(values=c(1),
                        labels=c("No Competition")) +
  geom_line(size = 1) +
  coord_cartesian(ylim=c(minY, maxY + 1), xlim=c(minX, maxX + 1)) +
  geom_vline(xintercept = monopolyCycle[[NO_COMPETITION]],
             linetype=2, size=1.5, color="black") +
  labs(x = "Round", y = "%",
       colour = "Treatment",
       shape = "Treatment",
       linetype = "Treatment") +
  theme(axis.text = element_text(size = 16, face = "bold", colour = "black"),
        axis.title = element_text(size = 18, face = "bold"),
        legend.title = element_blank(),
        legend.text = element_text(size=16, face = "bold"),
        legend.position = "top")

dev.off()


##
## NUMBER OF FIGHTS
##
png(file=paste(imagePath,"/NC-numberFights.png", sep=""),
    width=800, height=600)

minY <- 0
maxY <- ceiling(max(NC$numFights,CN$numFights,CP$numFights))

ggplot(data, aes(x = cycle, y = numFights,
                 color = factor(type),
                 shape = factor(type),
                 linetype = factor(type))) +
  scale_colour_manual(values=c("black"),
                      labels=c("No Competition")) +
  scale_shape_manual(values=c(17),
                     labels=c("No Competition")) +
  scale_linetype_manual(values=c(1),
                        labels=c("No Competition")) +
  geom_line(size = 1) +
  coord_cartesian(ylim=c(minY, maxY + 1), xlim=c(minX, maxX + 1)) +
  geom_vline(xintercept = monopolyCycle[[NO_COMPETITION]],
             linetype=2, size=1.5, color="black") +
  labs(x = "Round", y = "Number",
       colour = "Treatment",
       shape = "Treatment",
       linetype = "Treatment") +
  theme(axis.text = element_text(size = 16, face = "bold", colour = "black"),
        axis.title = element_text(size = 18, face = "bold"),
        legend.title = element_blank(),
        legend.text = element_text(size=16, face = "bold"),
        legend.position = "top")

dev.off()


##
## SEVERITY OF PUNISHMENT
##
png(file=paste(imagePath,"/NC-sevPunishment.png", sep=""),
    width=800, height=600)

minY <- 0
maxY <- ceiling(max(NC$sevPunishment,CN$sevPunishment,CP$sevPunishment))

ggplot(data, aes(x = cycle, y = sevPunishment,
                 color = factor(type),
                 shape = factor(type),
                 linetype = factor(type))) +
  scale_colour_manual(values=c("black"),
                      labels=c("No Competition")) +
  scale_shape_manual(values=c(17),
                     labels=c("No Competition")) +
  scale_linetype_manual(values=c(1),
                        labels=c("No Competition")) +
  geom_line(size = 1) +
  coord_cartesian(ylim=c(minY, maxY + 1), xlim=c(minX, maxX + 1)) +
  geom_vline(xintercept = monopolyCycle[[NO_COMPETITION]],
             linetype=2, size=1.5, color="black") +
  labs(x = "Round", y = "Severity",
       colour = "Treatment",
       shape = "Treatment",
       linetype = "Treatment") +
  theme(axis.text = element_text(size = 16, face = "bold", colour = "black"),
        axis.title = element_text(size = 18, face = "bold"),
        legend.title = element_blank(),
        legend.text = element_text(size=16, face = "bold"),
        legend.position = "top")

dev.off()


##
## Competition & Non-Protection
##
data <- CN

##
## NUMBER OF EXTORTERS
##
png(file=paste(imagePath,"/CN-numberExtorters.png", sep=""),
    width=800, height=600)

minY <- 0
maxY <- ceiling(max(NC$numExtorters,CN$numExtorters,CP$numExtorters))

ggplot(data, aes(x = cycle, y = numExtorters,
                 color = factor(type),
                 shape = factor(type),
                 linetype = factor(type))) +
  scale_colour_manual(values=c("red"),
                      labels=c("Competition")) +
  scale_shape_manual(values=c(17),
                     labels=c("Competition")) +
  scale_linetype_manual(values=c(1),
                        labels=c("Competition")) +
  geom_line(size = 1) +
  coord_cartesian(ylim=c(minY, maxY + 1), xlim=c(minX, maxX + 1)) +
  geom_vline(xintercept = monopolyCycle[[NO_PROTECTION]],
             linetype=2, size=1.5, color="black") +
  labs(x = "Round", y = "Number",
       colour = "Treatment",
       shape = "Treatment",
       linetype = "Treatment") +
  theme(axis.text = element_text(size = 16, face = "bold", colour = "black"),
        axis.title = element_text(size = 18, face = "bold"),
        legend.title = element_blank(),
        legend.text = element_text(size=16, face = "bold"),
        legend.position = "top")

dev.off()


##
## NUMBER OF TARGETS
##
png(file=paste(imagePath,"/CN-numberTargets.png", sep=""),
    width=800, height=600)

minY <- 0
maxY <- ceiling(max(NC$numTargets,CN$numTargets,CP$numTargets))

ggplot(data, aes(x = cycle, y = numTargets,
                 color = factor(type),
                 shape = factor(type),
                 linetype = factor(type))) +
  scale_colour_manual(values=c("red"),
                      labels=c("Competition")) +
  scale_shape_manual(values=c(17),
                     labels=c("Competition")) +
  scale_linetype_manual(values=c(1),
                        labels=c("Competition")) +
  geom_line(size = 1) +
  coord_cartesian(ylim=c(minY, maxY + 1), xlim=c(minX, maxX + 1)) +
  geom_vline(xintercept = monopolyCycle[[NO_PROTECTION]],
             linetype=2, size=1.5, color="black") +
  labs(x = "Round", y = "Number",
       colour = "Treatment",
       shape = "Treatment",
       linetype = "Treatment") +
  theme(axis.text = element_text(size = 16, face = "bold", colour = "black"),
        axis.title = element_text(size = 18, face = "bold"),
        legend.title = element_blank(),
        legend.text = element_text(size=16, face = "bold"),
        legend.position = "top")

dev.off()


##
## TARGET'S INCOME SPENT ON PAYING EXTORTION
##
png(file=paste(imagePath,"/CN-targetsIncome.png", sep=""),
    width=800, height=600)

minY <- 0
maxY <- ceiling(max(NC$targetsIncome,CN$targetsIncome,CP$targetsIncome))

ggplot(data, aes(x = cycle, y = targetsIncome,
                 color = factor(type),
                 shape = factor(type),
                 linetype = factor(type))) +
  scale_colour_manual(values=c("red"),
                      labels=c("Competition")) +
  scale_shape_manual(values=c(17),
                     labels=c("Competition")) +
  scale_linetype_manual(values=c(1),
                        labels=c("Competition")) +
  geom_line(size = 1) +
  coord_cartesian(ylim=c(minY, 100), xlim=c(minX, maxX + 1)) +
  geom_vline(xintercept = monopolyCycle[[NO_PROTECTION]],
             linetype=2, size=1.5, color="black") +
  labs(x = "Round", y = "%",
       colour = "Treatment",
       shape = "Treatment",
       linetype = "Treatment") +
  theme(axis.text = element_text(size = 16, face = "bold", colour = "black"),
        axis.title = element_text(size = 18, face = "bold"),
        legend.title = element_blank(),
        legend.text = element_text(size=16, face = "bold"),
        legend.position = "top")

dev.off()


##
## EXTORTIONS THAT RESULTED IN PUNISHMENT
##
png(file=paste(imagePath,"/CN-extortionPunished.png", sep=""),
    width=800, height=600)

minY <- 0
maxY <- ceiling(max(NC$extortionPunished,CN$extortionPunished,CP$extortionPunished))

ggplot(data, aes(x = cycle, y = extortionPunished,
                 color = factor(type),
                 shape = factor(type),
                 linetype = factor(type))) +
  scale_colour_manual(values=c("red"),
                      labels=c("Competition")) +
  scale_shape_manual(values=c(17),
                     labels=c("Competition")) +
  scale_linetype_manual(values=c(1),
                        labels=c("Competition")) +
  geom_line(size = 1) +
  coord_cartesian(ylim=c(minY, 100), xlim=c(minX, maxX + 1)) +
  geom_vline(xintercept = monopolyCycle[[NO_PROTECTION]],
             linetype=2, size=1.5, color="black") +
  labs(x = "Round", y = "%",
       colour = "Treatment",
       shape = "Treatment",
       linetype = "Treatment") +
  theme(axis.text = element_text(size = 16, face = "bold", colour = "black"),
        axis.title = element_text(size = 18, face = "bold"),
        legend.title = element_blank(),
        legend.text = element_text(size=16, face = "bold"),
        legend.position = "top")

dev.off()


##
## NUMBER OF FIGHTS
##
png(file=paste(imagePath,"/CN-numberFights.png", sep=""),
    width=800, height=600)

minY <- 0
maxY <- ceiling(max(NC$numFights,CN$numFights,CP$numFights))

ggplot(data, aes(x = cycle, y = numFights,
                 color = factor(type),
                 shape = factor(type),
                 linetype = factor(type))) +
  scale_colour_manual(values=c("red"),
                      labels=c("Competition")) +
  scale_shape_manual(values=c(17),
                     labels=c("Competition")) +
  scale_linetype_manual(values=c(1),
                        labels=c("Competition")) +
  geom_line(size = 1) +
  coord_cartesian(ylim=c(minY, maxY + 1), xlim=c(minX, maxX + 1)) +
  geom_vline(xintercept = monopolyCycle[[NO_PROTECTION]],
             linetype=2, size=1.5, color="black") +
  labs(x = "Round", y = "Number",
       colour = "Treatment",
       shape = "Treatment",
       linetype = "Treatment") +
  theme(axis.text = element_text(size = 16, face = "bold", colour = "black"),
        axis.title = element_text(size = 18, face = "bold"),
        legend.title = element_blank(),
        legend.text = element_text(size=16, face = "bold"),
        legend.position = "top")

dev.off()


##
## SEVERITY OF PUNISHMENT
##
png(file=paste(imagePath,"/CN-sevPunishment.png", sep=""),
    width=800, height=600)

minY <- 0
maxY <- ceiling(max(NC$sevPunishment,CN$sevPunishment,CP$sevPunishment))

ggplot(data, aes(x = cycle, y = sevPunishment,
                 color = factor(type),
                 shape = factor(type),
                 linetype = factor(type))) +
  scale_colour_manual(values=c("red"),
                      labels=c("Competition")) +
  scale_shape_manual(values=c(17),
                     labels=c("Competition")) +
  scale_linetype_manual(values=c(1),
                        labels=c("Competition")) +
  geom_line(size = 1) +
  coord_cartesian(ylim=c(minY, maxY + 1), xlim=c(minX, maxX + 1)) +
  geom_vline(xintercept = monopolyCycle[[NO_PROTECTION]],
             linetype=2, size=1.5, color="black") +
  labs(x = "Round", y = "Severity",
       colour = "Treatment",
       shape = "Treatment",
       linetype = "Treatment") +
  theme(axis.text = element_text(size = 16, face = "bold", colour = "black"),
        axis.title = element_text(size = 18, face = "bold"),
        legend.title = element_blank(),
        legend.text = element_text(size=16, face = "bold"),
        legend.position = "top")

dev.off()


##
## Competition & Protection
##
data <- CP

##
## NUMBER OF EXTORTERS
##
png(file=paste(imagePath,"/CP-numberExtorters.png", sep=""),
    width=800, height=600)

minY <- 0
maxY <- ceiling(max(NC$numExtorters,CN$numExtorters,CP$numExtorters))

ggplot(data, aes(x = cycle, y = numExtorters,
                 color = factor(type),
                 shape = factor(type),
                 linetype = factor(type))) +
  scale_colour_manual(values=c("blue"),
                      labels=c("Competition & Protection")) +
  scale_shape_manual(values=c(17),
                     labels=c("Competition & Protection")) +
  scale_linetype_manual(values=c(1),
                        labels=c("Competition & Protection")) +
  geom_line(size = 1) +
  coord_cartesian(ylim=c(minY, maxY + 1), xlim=c(minX, maxX + 1)) +
  geom_vline(xintercept = monopolyCycle[[PROTECTION]],
             linetype=2, size=1.5, color="black") +
  labs(x = "Round", y = "Number",
       colour = "Treatment",
       shape = "Treatment",
       linetype = "Treatment") +
  theme(axis.text = element_text(size = 16, face = "bold", colour = "black"),
        axis.title = element_text(size = 18, face = "bold"),
        legend.title = element_blank(),
        legend.text = element_text(size=16, face = "bold"),
        legend.position = "top")

dev.off()


##
## NUMBER OF TARGETS
##
png(file=paste(imagePath,"/CP-numberTargets.png", sep=""),
    width=800, height=600)

minY <- 0
maxY <- ceiling(max(NC$numTargets,CN$numTargets,CP$numTargets))

ggplot(data, aes(x = cycle, y = numTargets,
                 color = factor(type),
                 shape = factor(type),
                 linetype = factor(type))) +
  scale_colour_manual(values=c("blue"),
                      labels=c("Competition & Protection")) +
  scale_shape_manual(values=c(17),
                     labels=c("Competition & Protection")) +
  scale_linetype_manual(values=c(1),
                        labels=c("Competition & Protection")) +
  geom_line(size = 1) +
  coord_cartesian(ylim=c(minY, maxY + 1), xlim=c(minX, maxX + 1)) +
  geom_vline(xintercept = monopolyCycle[[PROTECTION]],
             linetype=2, size=1.5, color="black") +
  labs(x = "Round", y = "Number",
       colour = "Treatment",
       shape = "Treatment",
       linetype = "Treatment") +
  theme(axis.text = element_text(size = 16, face = "bold", colour = "black"),
        axis.title = element_text(size = 18, face = "bold"),
        legend.title = element_blank(),
        legend.text = element_text(size=16, face = "bold"),
        legend.position = "top")

dev.off()


##
## TARGET'S INCOME SPENT ON PAYING EXTORTION
##
png(file=paste(imagePath,"/CP-targetsIncome.png", sep=""),
    width=800, height=600)

minY <- 0
maxY <- ceiling(max(NC$targetsIncome,CN$targetsIncome,CP$targetsIncome))

ggplot(data, aes(x = cycle, y = targetsIncome,
                 color = factor(type),
                 shape = factor(type),
                 linetype = factor(type))) +
  scale_colour_manual(values=c("blue"),
                      labels=c("Competition & Protection")) +
  scale_shape_manual(values=c(17),
                     labels=c("Competition & Protection")) +
  scale_linetype_manual(values=c(1),
                        labels=c("Competition & Protection")) +
  geom_line(size = 1) +
  coord_cartesian(ylim=c(minY, 100), xlim=c(minX, maxX + 1)) +
  geom_vline(xintercept = monopolyCycle[[PROTECTION]],
             linetype=2, size=1.5, color="black") +
  labs(x = "Round", y = "%",
       colour = "Treatment",
       shape = "Treatment",
       linetype = "Treatment") +
  theme(axis.text = element_text(size = 16, face = "bold", colour = "black"),
        axis.title = element_text(size = 18, face = "bold"),
        legend.title = element_blank(),
        legend.text = element_text(size=16, face = "bold"),
        legend.position = "top")

dev.off()


##
## EXTORTIONS THAT RESULTED IN PUNISHMENT
##
png(file=paste(imagePath,"/CP-extortionPunished.png", sep=""),
    width=800, height=600)

minY <- 0
maxY <- ceiling(max(NC$extortionPunished,CN$extortionPunished,CP$extortionPunished))

ggplot(data, aes(x = cycle, y = extortionPunished,
                 color = factor(type),
                 shape = factor(type),
                 linetype = factor(type))) +
  scale_colour_manual(values=c("blue"),
                      labels=c("Competition & Protection")) +
  scale_shape_manual(values=c(17),
                     labels=c("Competition & Protection")) +
  scale_linetype_manual(values=c(1),
                        labels=c("Competition & Protection")) +
  geom_line(size = 1) +
  coord_cartesian(ylim=c(minY, 100), xlim=c(minX, maxX + 1)) +
  geom_vline(xintercept = monopolyCycle[[PROTECTION]],
             linetype=2, size=1.5, color="black") +
  labs(x = "Round", y = "%",
       colour = "Treatment",
       shape = "Treatment",
       linetype = "Treatment") +
  theme(axis.text = element_text(size = 16, face = "bold", colour = "black"),
        axis.title = element_text(size = 18, face = "bold"),
        legend.title = element_blank(),
        legend.text = element_text(size=16, face = "bold"),
        legend.position = "top")

dev.off()


##
## NUMBER OF FIGHTS
##
png(file=paste(imagePath,"/CP-numberFights.png", sep=""),
    width=800, height=600)

minY <- 0
maxY <- ceiling(max(NC$numFights,CN$numFights,CP$numFights))

ggplot(data, aes(x = cycle, y = numFights,
                 color = factor(type),
                 shape = factor(type),
                 linetype = factor(type))) +
  scale_colour_manual(values=c("blue"),
                      labels=c("Competition & Protection")) +
  scale_shape_manual(values=c(17),
                     labels=c("Competition & Protection")) +
  scale_linetype_manual(values=c(1),
                        labels=c("Competition & Protection")) +
  geom_line(size = 1) +
  coord_cartesian(ylim=c(minY, maxY + 1), xlim=c(minX, maxX + 1)) +
  geom_vline(xintercept = monopolyCycle[[PROTECTION]],
             linetype=2, size=1.5, color="black") +
  labs(x = "Round", y = "Number",
       colour = "Treatment",
       shape = "Treatment",
       linetype = "Treatment") +
  theme(axis.text = element_text(size = 16, face = "bold", colour = "black"),
        axis.title = element_text(size = 18, face = "bold"),
        legend.title = element_blank(),
        legend.text = element_text(size=16, face = "bold"),
        legend.position = "top")

dev.off()


##
## SEVERITY OF PUNISHMENT
##
png(file=paste(imagePath,"/CP-sevPunishment.png", sep=""),
    width=800, height=600)

minY <- 0
maxY <- ceiling(max(NC$sevPunishment,CN$sevPunishment,CP$sevPunishment))

ggplot(data, aes(x = cycle, y = sevPunishment,
                 color = factor(type),
                 shape = factor(type),
                 linetype = factor(type))) +
  scale_colour_manual(values=c("blue"),
                      labels=c("Competition & Protection")) +
  scale_shape_manual(values=c(17),
                     labels=c("Competition & Protection")) +
  scale_linetype_manual(values=c(1),
                        labels=c("Competition & Protection")) +
  geom_line(size = 1) +
  coord_cartesian(ylim=c(minY, maxY + 1), xlim=c(minX, maxX + 1)) +
  geom_vline(xintercept = monopolyCycle[[PROTECTION]],
             linetype=2, size=1.5, color="black") +
  labs(x = "Round", y = "Severity",
       colour = "Treatment",
       shape = "Treatment",
       linetype = "Treatment") +
  theme(axis.text = element_text(size = 16, face = "bold", colour = "black"),
        axis.title = element_text(size = 18, face = "bold"),
        legend.title = element_blank(),
        legend.text = element_text(size=16, face = "bold"),
        legend.position = "top")

dev.off()