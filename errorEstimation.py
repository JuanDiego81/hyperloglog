from typing import List, Dict, Tuple
import numpy as np
import csv
import matplotlib.pyplot as plt

# For each hyperloglog method, see how it predicted a list of 1 million distinct elements
# The experiment did this a 100 times for each m so each m should have a length of 100
estimates_by_m:Dict[int, list[int]] = {256: [], 1024:[], 4096: []}

# The cardinality for each list
N = 1_000_000

with open("results.csv", "r") as file:
    reader = csv.DictReader(file)
    print(reader)
    for row in reader:
        m:int = int(row["m"])
        estimation:int = int(row["Estimation of distinct elements"])
        estimates_by_m[m].append(estimation)

# Ensure that 100 elements are in the list for each m
for key, value in estimates_by_m.items():
    if len(value) != 100:
        raise ValueError("Check the length of the lists in estimates_by_m")

m_values = [256, 1024, 4096]

def createErrorTableFile(file):
    with open(file, "w") as file:
            writer = csv.DictWriter(file,
            fieldnames = ["m","n","sigma","mean_estimation","std_dev", "within_1σ", "within_2σ"])
            writer.writeheader()
            for m in m_values:
                estimates = np.array(estimates_by_m[m])
                sigma:float = 1.04 / np.sqrt(m)

                lower_1sigma:float = N * (1 - sigma)
                upper_1sigma:float = N * (1 + sigma)

                lower_2sigma:float = N * (1 - 2 * sigma)
                upper_2sigma:float = N * (1 + 2 * sigma)

                #Count how many elements in estimates fall within the sigma range
                within_1sigma:int = np.sum((estimates >= lower_1sigma) & (estimates <= upper_1sigma))
                within_2sigma:int = np.sum((estimates >= lower_2sigma) & (estimates <= upper_2sigma))
                
                mean = np.mean(estimates)
                std_dev = np.std(estimates)

                writer.writerow({
                    "m": m,
                    "n": N,
                    "sigma":sigma,
                    "mean_estimation": mean,
                    "std_dev": round(std_dev,4),
                    "within_1σ": str(within_1sigma) + "/" + str(len(estimates)) ,
                    "within_2σ": str(within_2sigma) + "/" + str(len(estimates)) 
                })

# createErrorTableFile("sigma_table_results.csv")

def createHistograms ():
    for m in m_values:
        #estimation error formula (est-N)/N
        estimation_errors = [(est-N)/N for est in estimates_by_m[m]]
        minError = min(estimation_errors)
        maxError = max(estimation_errors)
        plt.figure(figsize=(5,3)) 
        plt.hist(estimation_errors,bins=np.arange(minError - 0.1 , maxError +0.1, 0.01), color = "skyblue", edgecolor = "black", alpha = 0.7)
        plt.axvline(0, color ='green', linestyle="--", label= "True n")
        plt.xlabel("Estimation Error")
        plt.ylabel("Frequency")
        plt.legend()
        plt.tight_layout()
        plt.savefig(f"m{m}_histogram.pdf")

createHistograms()



            