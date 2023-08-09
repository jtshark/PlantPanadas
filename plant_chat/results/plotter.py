import os
import re
import numpy as np

if __name__ == '__main__':

    for name in os.listdir("."):
        full_path = os.path.join(".", name)
        if os.path.isdir(full_path):
            if "venv" in name or ".idea" in name or "points" in name:
                continue

            timings = []
            for task in os.listdir(full_path):
                task_path = os.path.join(full_path, task, "timings.txt")
                with open(task_path, "r") as file:
                    content = file.read()

                numbers = re.findall(r'\d+', content)

                # Convert the strings to integers and sum them
                total = sum(map(int, numbers))
                timings.append(total)

            print(f"{name} timings: {np.mean(timings) / 1000}s")
