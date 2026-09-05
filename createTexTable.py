import csv

def csv_to_latex_table(csv_file, output_file):
    
    with open(csv_file, "r", newline='') as f:
        reader = csv.DictReader(f)
        rows = list(reader)
        headers = reader.fieldnames

    # Write LaTeX table
    with open(output_file, "w") as f:
        f.write("\\renewcommand{\\arraystretch}{1.2} % slightly taller rows\n")
        f.write("\\begin{tabular}{@{} r S[table-format=7.2] S[table-format=1.5] "
                "S[table-format=8.2] S[table-format=6.2] c c @{} }\n")
        f.write("\\toprule\n")

        # Headers
        header_line = []
        for h in headers:
            if "sigma" in h.lower():
                header_line.append(f"${h}$")
            else:
                header_line.append(h.replace("_", " ").title())
        f.write(" & ".join(header_line) + " \\\\\n")

        f.write("\\midrule\n")

        # Table rows
        for row in rows:
            row_values = [row[h] for h in headers]
            f.write(" & ".join(row_values) + " \\\\\n")

        f.write("\\bottomrule\n")
        f.write("\\end{tabular}\n")

csv_to_latex_table("sigma_table_results.csv", "table.tex")
