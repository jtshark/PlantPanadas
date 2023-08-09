import os
import re
import numpy as np
import matplotlib.pyplot as plt

if __name__ == '__main__':

    t = {}
    price = {"ChatGPT-3-CoT": 0.6, "ChatGPT-4-IoP": 3.2, "ChatGPT-4-CoT": 15}
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
            t[name] = np.mean(timings) / 1000

    order = ["ChatGPT-3-CoT", "ChatGPT-4-IoP", "ChatGPT-4-CoT"]

    labels = [o for o in order]
    time_values = [t[o] for o in order]
    price_values = [price[o] for o in order]

    x = np.arange(len(labels))  # the label locations
    width = 0.35  # the width of the bars

    fig, ax1 = plt.subplots()

    # Create bars for 'time' on the first y-axis
    rects1 = ax1.bar(x - width / 2, time_values, width, label='Time')

    # Create the second y-axis
    ax2 = ax1.twinx()

    # Create bars for 'price' on the second y-axis
    rects2 = ax2.bar(x + width / 2, price_values, width, label='Price', color='green')

    # Add some text for labels, title and custom x-axis tick labels, etc.
    ax1.set_xlabel('LLM Type')
    ax1.set_ylabel('Zeit pro Anfrage [s]', color='blue')
    ax2.set_ylabel('Preis pro Anfrage [cent]', color='green')

    ax1.set_xticks(x)
    ax1.set_xticklabels(labels)

    fig.tight_layout()

    plt.savefig("time_cost_plot.png", dpi=300)
    plt.show()
