
from typing import List, Dict, Tuple
import numpy as np
import subprocess
import time
import csv

def run_java(jar: str, arg: str, input: str)->str:
    p = subprocess.Popen(['java','-jar',jar,arg],
        stdin=subprocess.PIPE,
        stdout=subprocess.PIPE)
    (output,_) = p.communicate(input.encode('utf-8'))
    return output.decode('utf-8')

def distinctRandomIntegers(n: int, seed: int) -> list[int]:
    setOfInts = set()
    rng = np.random.default_rng(seed)
    
    while len(setOfInts) < n:
        setOfInts.add(rng.integers(0, 2**32))
    
    return list(setOfInts)

def benchmark(numbers:list[int],algorithm: str, jar: str)->int:
    #Scanner reads numbers line by line so split the list accordingly
    input_string = "\n".join(map(str,numbers))
    cardinality = run_java(jar, algorithm, input_string)
    return int(cardinality.strip())
    

SEED:int = 314159
#To get 1_000_000 million distinct elements for testing
MAX_VALUE = 10**6
# [256, 1024, 4096]
INSTANCES: List[Tuple[str,str]] = [
    ('hyperloglog256', 'hyperloglog/app/build/libs/app.jar'),
    ('hyperloglog1024', 'hyperloglog/app/build/libs/app.jar'),
    ('hyperloglog4096', 'hyperloglog/app/build/libs/app.jar')
]

if __name__ == '__main__':
    with open('results.csv','w') as f:
        writer = csv.DictWriter(f,
            fieldnames = ['method','m','Estimation of distinct elements'])
        writer.writeheader()
        for algorithm, jar in INSTANCES:
            m:int = (int)(''.join(filter(str.isdigit, algorithm)))
            for trial in range(100):
                #Vary the seed in each iteration to get distinct items in each iteration
                # By reusing curr to store the random list we save a bunch of memory for my poor 8gb ram
                INPUT_DATA = distinctRandomIntegers(MAX_VALUE, SEED + trial)
                cardinality = benchmark(INPUT_DATA, algorithm, jar)
                writer.writerow({
                    "method": algorithm,
                    "m": m,
                    "Estimation of distinct elements": cardinality
                })
                


