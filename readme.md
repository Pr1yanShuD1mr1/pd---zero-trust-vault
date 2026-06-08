# Project244: Zero-Trust File Encryption Vault

Project244 is a lightweight, zero-trust security architecture and data isolation protection system written in Java. 
It is designed to act as a secure vault that guards sensitive files against unauthorized retrieval, exfiltration, and ransomware attacks. 

The system leverages a split-block, single-responsibility distributed file architecture alongside AES-256 bit block encryption in standard Electronic Codebook (ECB) mode with PKCS5 padding.

---

## Core Architecture & Security Principles

* **Zero-Trust Engine:** No assumptions of trust are made for any incoming HTTP connection. Every transaction relies entirely on an un-linkable, zero-knowledge cryptographic signature token (`.key` file) generated during ingestion.
* **Cryptographic Data Fragmentation:** Upon payload upload, files are read sequentially using an isolated input stream buffer. Instead of preserving the monolithic structure of a file, the application chunks it into separate raw byte streams, encrypts each section, and distributes them randomly to unique 8-digit randomized block IDs.
* **Opaque Reference Mapping:** File headers, filenames, extensions, and spatial reconstruction pathways are completely omitted from the storage disk directories. This structural layout is recorded inside an external transaction ledger (`records.txt`) that maps individual cryptographic keys to specific, scattered data fragment strings.
* **Strict Native Stream Processing:** The platform avoids structural envelope parsing (like Multipart/FormData) at the boundary network layers, processing file chunks as pure, unfiltered binary arrays to preserve the integrity of the data stream.

---

## Tech Stack & Components

* **Backend:** Java Core, `com.sun.net.httpserver.HttpServer` (Nio Base)
* **Cryptography:** `javax.crypto` (AES-256 bit engine specification)
* **Frontend:** HTML5, CSS3 (Neo-Brutalist design tokens), Native JavaScript Fetch API
* **Protocol Layer:** Cross-Origin Resource Sharing (CORS) with stateful HTTP OPTIONS preflight validation handlers.

---
