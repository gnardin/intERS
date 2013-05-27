library(bigmemory)
library(biganalytics)

x <- big.matrix(5, 3, type="double", init=0, 
                dimnames=list(NULL, c("a", "b", "c")))
x[,1] <- seq(1:5)
x[,2] <- c(1,2,1,4,4)
x[,3] <- c(10,20,5,40,30)
x[,]

y1 <- x[mwhich(x, "b", 1, "eq"),drop=FALSE]
class(y1)
y1
colmean(y1)

y2 <- x[mwhich(x, "b", 2, "eq"),drop=FALSE]
class(y2)
y2
colmean(as.big.matrix(y2))

x
x[,]
x[,1] <- seq(1:10)
x[,2] <- sample(1:4, 10, replace=TRUE)
x[,3] <- sample(1:20, 10, replace=FALSE)
x[,]
x[mwhich(x, "alpha", 3, "eq")]
mean(x)
colmean(x)
summary(x)
colmean(as.big.matrix(x[mwhich(x, "alpha", 3, "eq"),]))

colavg <- function(n,x) {
  if(length(n) == 1){
    result <- colmean(as.big.matrix(t(as.matrix(x[,]))))
  }else{
    result <- colmean(x)
  }
  result
}

i <- bigsplit(x, "b")
r <- foreach(index = i, .combine=rbind) %dopar% {colmean(as.big.matrix(x[index, which(!grepl("b", colnames(x))),
                                                                         drop=FALSE])) }