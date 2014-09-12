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
basePath <- paste("/data/workspace/gloders/intERS/output/everyone/",sep="")
dir.create(file.path(basePath, "imagesCOLOR"), showWarnings = FALSE)
imagePath <- paste(basePath,"imagesCOLOR", sep="")

###
### Constants
###
NO_COMPETITION <- 1
NO_PROTECTION <- 2
PROTECTION <- 3
  
RANGE <- 1:700
  
CYCLES <- 300
SIMULATIONS <- 50
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

##
## Non-Competition and Competition & Non-Protection
##
data <- rbind(NC, CN)

##
## NUMBER OF EXTORTERS
##
png(file=paste(imagePath,"/NC-CN-numberExtorters.png", sep=""),
    width=800, height=600)

minY <- 0
maxY <- ceiling(max(NC$numExtorters,CN$numExtorters))

ggplot(data, aes(x = cycle, y = numExtorters,
                 color = factor(type),
                 shape = factor(type),
                 linetype = factor(type))) +
  scale_colour_manual(values=c("black","black"),
                      labels=c("No-Competition", "Competition & No-Strong-Protection")) +
  scale_shape_manual(values=c(17,15),
                     labels=c("No-Competition", "Competition & No-Strong-Protection")) +
  scale_linetype_manual(values=c(3,1),
                        labels=c("No-Competition", "Competition & No-Strong-Protection")) +
  geom_line(size = 1) + coord_cartesian(ylim=c(minY, maxY + 1)) +
  geom_vline(xintercept = min(monopolyCycle[[1]], monopolyCycle[[2]]),
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
png(file=paste(imagePath,"/NC-CN-numberTargets.png", sep=""),
    width=800, height=600)

minY <- 0
maxY <- ceiling(max(NC$numTargets,CN$numTargets))

ggplot(data, aes(x = cycle, y = numTargets,
                 color = factor(type),
                 shape = factor(type),
                 linetype = factor(type))) +
  scale_colour_manual(values=c("black","black"),
                      labels=c("No-Competition", "Competition & No-Strong-Protection")) +
  scale_shape_manual(values=c(17,15),
                     labels=c("No-Competition", "Competition & No-Strong-Protection")) +
  scale_linetype_manual(values=c(3,1),
                        labels=c("No-Competition", "Competition & No-Strong-Protection")) +
  geom_line(size = 1) + coord_cartesian(ylim=c(minY, maxY + 1)) +
  geom_vline(xintercept = min(monopolyCycle[[1]], monopolyCycle[[2]]),
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
png(file=paste(imagePath,"/NC-CN-targetsIncome.png", sep=""),
    width=800, height=600)

minY <- 0
maxY <- ceiling(max(NC$targetsIncome,CN$targetsIncome))

ggplot(data, aes(x = cycle, y = targetsIncome,
                 color = factor(type),
                 shape = factor(type),
                 linetype = factor(type))) +
  scale_colour_manual(values=c("black","black"),
                      labels=c("No-Competition", "Competition & No-Strong-Protection")) +
  scale_shape_manual(values=c(17,15),
                     labels=c("No-Competition", "Competition & No-Strong-Protection")) +
  scale_linetype_manual(values=c(3,1),
                        labels=c("No-Competition", "Competition & No-Strong-Protection")) +
  geom_line(size = 1) + coord_cartesian(ylim=c(minY, 100)) +
  geom_vline(xintercept = min(monopolyCycle[[1]], monopolyCycle[[2]]),
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
png(file=paste(imagePath,"/NC-CN-extortionPunished.png", sep=""),
    width=800, height=600)

minY <- 0
maxY <- ceiling(max(NC$extortionPunished,CN$extortionPunished))

ggplot(data, aes(x = cycle, y = extortionPunished,
                 color = factor(type),
                 shape = factor(type),
                 linetype = factor(type))) +
  scale_colour_manual(values=c("black","black"),
                      labels=c("No-Competition", "Competition & No-Strong-Protection")) +
  scale_shape_manual(values=c(17,15),
                     labels=c("No-Competition", "Competition & No-Strong-Protection")) +
  scale_linetype_manual(values=c(3,1),
                        labels=c("No-Competition", "Competition & No-Strong-Protection")) +
  geom_line(size = 1) + coord_cartesian(ylim=c(minY, 100)) +
  geom_vline(xintercept = min(monopolyCycle[[1]], monopolyCycle[[2]]),
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
png(file=paste(imagePath,"/NC-CN-numberFights.png", sep=""),
    width=800, height=600)

minY <- 0
maxY <- ceiling(max(NC$numFights,CN$numFights))

ggplot(data, aes(x = cycle, y = numFights,
                 color = factor(type),
                 shape = factor(type),
                 linetype = factor(type))) +
  scale_colour_manual(values=c("black","black"),
                      labels=c("Competition & No-Strong-Protection", "Competition & Strong-Protection")) +
  scale_shape_manual(values=c(17,15),
                     labels=c("Competition & No-Strong-Protection", "Competition & Strong-Protection")) +
  scale_linetype_manual(values=c(3,1),
                        labels=c("Competition & No-Strong-Protection", "Competition & Strong-Protection")) +
  geom_line(size = 1) + coord_cartesian(ylim=c(minY, maxY + 1)) +
  geom_vline(xintercept = min(monopolyCycle[[1]], monopolyCycle[[2]]),
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
png(file=paste(imagePath,"/NC-CN-sevPunishment.png", sep=""),
    width=800, height=600)

minY <- 0
maxY <- ceiling(max(NC$sevPunishment,CN$sevPunishment))

ggplot(data, aes(x = cycle, y = sevPunishment,
                 color = factor(type),
                 shape = factor(type),
                 linetype = factor(type))) +
  scale_colour_manual(values=c("black","black"),
                      labels=c("Competition & No-Strong-Protection", "Competition & Strong-Protection")) +
  scale_shape_manual(values=c(17,15),
                     labels=c("Competition & No-Strong-Protection", "Competition & Strong-Protection")) +
  scale_linetype_manual(values=c(3,1),
                        labels=c("Competition & No-Strong-Protection", "Competition & Strong-Protection")) +
  geom_line(size = 1) + coord_cartesian(ylim=c(minY, maxY + 1)) +
  geom_vline(xintercept = min(monopolyCycle[[1]], monopolyCycle[[2]]),
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
## Competition & Non-Protection and Competition & Protection
##
data <- rbind(CN, CP)

##
## NUMBER OF EXTORTERS
##
png(file=paste(imagePath,"/CN-CP-numberExtorters.png", sep=""),
    width=800, height=600)

minY <- 0
maxY <- ceiling(max(NC$numExtorters,CN$numExtorters))

ggplot(data, aes(x = cycle, y = numExtorters,
                 color = factor(type),
                 shape = factor(type),
                 linetype = factor(type))) +
  scale_colour_manual(values=c("black","black"),
                      labels=c("Competition & No-Strong-Protection", "Competition & Strong-Protection")) +
  scale_shape_manual(values=c(17,15),
                     labels=c("Competition & No-Strong-Protection", "Competition & Strong-Protection")) +
  scale_linetype_manual(values=c(3,1),
                        labels=c("Competition & No-Strong-Protection", "Competition & Strong-Protection")) +
  geom_line(size = 1) + coord_cartesian(ylim=c(minY, maxY + 1)) +
  geom_vline(xintercept = min(monopolyCycle[[2]], monopolyCycle[[3]]),
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
png(file=paste(imagePath,"/CN-CP-numberTargets.png", sep=""),
    width=800, height=600)

minY <- 0
maxY <- ceiling(max(NC$numTargets,CN$numTargets))

ggplot(data, aes(x = cycle, y = numTargets,
                 color = factor(type),
                 shape = factor(type),
                 linetype = factor(type))) +
  scale_colour_manual(values=c("black","black"),
                      labels=c("Competition & No-Strong-Protection", "Competition & Strong-Protection")) +
  scale_shape_manual(values=c(17,15),
                     labels=c("Competition & No-Strong-Protection", "Competition & Strong-Protection")) +
  scale_linetype_manual(values=c(3,1),
                        labels=c("Competition & No-Strong-Protection", "Competition & Strong-Protection")) +
  geom_line(size = 1) + coord_cartesian(ylim=c(minY, maxY + 1)) +
  geom_vline(xintercept = min(monopolyCycle[[2]], monopolyCycle[[3]]),
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
png(file=paste(imagePath,"/CN-CP-targetsIncome.png", sep=""),
    width=800, height=600)

minY <- 0
maxY <- ceiling(max(NC$targetsIncome,CN$targetsIncome))

ggplot(data, aes(x = cycle, y = targetsIncome,
                 color = factor(type),
                 shape = factor(type),
                 linetype = factor(type))) +
  scale_colour_manual(values=c("black","black"),
                      labels=c("Competition & No-Strong-Protection", "Competition & Strong-Protection")) +
  scale_shape_manual(values=c(17,15),
                     labels=c("Competition & No-Strong-Protection", "Competition & Strong-Protection")) +
  scale_linetype_manual(values=c(3,1),
                        labels=c("Competition & No-Strong-Protection", "Competition & Strong-Protection")) +
  geom_line(size = 1) + coord_cartesian(ylim=c(minY, 100)) +
  geom_vline(xintercept = min(monopolyCycle[[2]], monopolyCycle[[3]]),
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
png(file=paste(imagePath,"/CN-CP-extortionPunished.png", sep=""),
    width=800, height=600)

minY <- 0
maxY <- ceiling(max(NC$extortionPunished,CN$extortionPunished))

ggplot(data, aes(x = cycle, y = extortionPunished,
                 color = factor(type),
                 shape = factor(type),
                 linetype = factor(type))) +
  scale_colour_manual(values=c("black","black"),
                      labels=c("Competition & No-Strong-Protection", "Competition & Strong-Protection")) +
  scale_shape_manual(values=c(17,15),
                     labels=c("Competition & No-Strong-Protection", "Competition & Strong-Protection")) +
  scale_linetype_manual(values=c(3,1),
                        labels=c("Competition & No-Strong-Protection", "Competition & Strong-Protection")) +
  geom_line(size = 1) + coord_cartesian(ylim=c(minY, 100)) +
  geom_vline(xintercept = min(monopolyCycle[[2]], monopolyCycle[[3]]),
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
png(file=paste(imagePath,"/CN-CP-numberFights.png", sep=""),
    width=800, height=600)

minY <- 0
maxY <- ceiling(max(CN$numFights,CP$numFights))

ggplot(data, aes(x = cycle, y = numFights,
                 color = factor(type),
                 shape = factor(type),
                 linetype = factor(type))) +
  scale_colour_manual(values=c("black","black"),
                      labels=c("Competition & No-Strong-Protection", "Competition & Strong-Protection")) +
  scale_shape_manual(values=c(17,15),
                     labels=c("Competition & No-Strong-Protection", "Competition & Strong-Protection")) +
  scale_linetype_manual(values=c(3,1),
                        labels=c("Competition & No-Strong-Protection", "Competition & Strong-Protection")) +
  geom_line(size = 1) + coord_cartesian(ylim=c(minY, maxY + 1)) +
  geom_vline(xintercept = min(monopolyCycle[[2]], monopolyCycle[[3]]),
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
png(file=paste(imagePath,"/CN-CP-sevPunishment.png", sep=""),
    width=800, height=600)

minY <- 0
maxY <- ceiling(max(CN$sevPunishment,CP$sevPunishment))

ggplot(data, aes(x = cycle, y = sevPunishment,
                 color = factor(type),
                 shape = factor(type),
                 linetype = factor(type))) +
  scale_colour_manual(values=c("black","black"),
                      labels=c("Competition & No-Strong-Protection", "Competition & Strong-Protection")) +
  scale_shape_manual(values=c(17,15),
                     labels=c("Competition & No-Strong-Protection", "Competition & Strong-Protection")) +
  scale_linetype_manual(values=c(3,1),
                        labels=c("Competition & No-Strong-Protection", "Competition & Strong-Protection")) +
  geom_line(size = 1) + coord_cartesian(ylim=c(minY, maxY + 1)) +
  geom_vline(xintercept = min(monopolyCycle[[2]], monopolyCycle[[3]]),
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