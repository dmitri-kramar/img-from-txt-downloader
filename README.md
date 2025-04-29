# 📂 ImageFromTxtDownloader

**ImageFromTxtDownloader** is a simple Java CLI utility that scans the specified directory 
(or the current one by default) for text files (`.txt`, `.csv`, `.log`, `.rtf`), extracts image links, 
and downloads those images into automatically created folders.

---

## ✨ Features

- Scans the current directory for text files (`.txt`, `.csv`, `.log`, `.rtf`).
- Extracts links pointing to image files (`.jpg`, `.jpeg`, `.png`, `.gif`, `.webp`, `.bmp`).
- Downloads each found image into a folder named after the text file.
- Skips broken links without interrupting the entire process.
- Displays downloaded file paths and a summary report.

---

## 📝 Requirements

- Java 8 or higher

---

## 🔄 How to Use

1. **Clone or download** the project.
2. **Place your `.txt`, `.csv`, `.log`, or `.rtf` files** with image URLs into the desired folder.
3. **Compile the program:**

```bash
javac src/ImageDownloader.java
```

4. **Run the program:**

### Run with default behavior (scan current directory):

```bash
java -cp src ImageDownloader
```

### Run with a specific directory:

```bash
java -cp src ImageDownloader path/to/your/folder
```

---

## 📚 Example

Suppose you have a file `images.txt` with the following content:

```
https://example.com/photo1.jpg
https://example.com/photo2.png
https://example.com/photo3.gif
```

Running the program will:
- Create a folder named `images`
- Download all listed images into that folder

---

## 🔹 Notes

- You can specify a directory as an argument or use the current one.
- If no eligible text files are found, the program will print a warning and exit without an error.
- Broken or unreachable links are skipped with error messages displayed in the console.
- Already existing files will be overwritten.

---

© 2025 github.com/dmitri-kramar
