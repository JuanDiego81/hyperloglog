import matplotlib.pyplot as plt

TOTAL = 1_000_000
def counter (): #returns a dictionary with an int key and the number of times the key is repeated in the file
    counts = {}
    with open('./pHashOutput.txt') as file:
        for line in file:
            value = int(line.strip())
            if value in counts:
                counts[value] += 1
            else:
                counts[value] = 1
    return counts


probs = counter()
for key, value in probs.items():
    #normalize by dividing result by 10^6
    probs[key] = value/TOTAL


#create plot 
sortedKeys = sorted(probs.keys()) #x axis
empirical = [] #y axis
for num in sortedKeys:
    empirical.append(probs[num])

theoretical_match = []

for num in sortedKeys:
    # test if it satisfies Pr[(y) = i] = 2^-i
    theoretical_match.append(2**(-num))

def plot_graph (fileName):
    plt.figure(figsize = (8,5))
    plt.bar(sortedKeys, empirical, alpha=0.6, label = "Hash Output")
    plt.plot(sortedKeys, theoretical_match, marker = "o", label = "Theoretical 2^-i")
    plt.xlabel("p value: Number of Leading Zeros")
    plt.ylabel("Probability")
    plt.title("Distribution of p(h(x)) for 1,000,000 integers ")
    plt.legend()
    plt.yscale("log")   
    plt.tight_layout()
    plt.savefig(fileName)

plot_graph("hash_distribution.pdf")