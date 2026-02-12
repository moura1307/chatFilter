# ☕ Chat Filter

A robust, high-performance Minecraft chat filter designed for the modern server environment. This plugin offers a dual-layered protection system, allowing individual players to choose their experience while giving administrators total control during sensitive times.

## 🚀 Features

* **Dynamic Wordlist:** Automatically downloads and maintains a list of 2,800+ bad words from academic sources (CMU).
* **Intelligent "Hell" Protection:** Uses a local `safewords.txt` whitelist to ensure words like "Hello," "Shell," and "Grass" are never accidentally censored.
* **Bypass Prevention:** Advanced regex detection catches players trying to bypass the filter using symbols like `f.u.c.k`, `f-u-c-k`, or `f_u_c_k`.
* **Attached Word Detection:** Catches bad words even when they are attached to other text (e.g., `fucklol`).
* **Dual-Layer Filtering:**
    * **Player Level:** Users can opt-in/out of the filter using `/togglefilter`.
    * **Global Level:** Admins can force the filter on for the entire server during raids or events.
* **High Performance:** Optimized with asynchronous file loading and smart skipping to ensure zero impact on server TPS.
* **Persistence:** All player preferences, global settings, and wordlists are saved locally to `.txt` and `.yml` files.

## 🛠 Commands & Permissions

| Command | Description | Permission |
| :--- | :--- | :--- |
| `/togglefilter <true/false>` | Turn your personal chat filter on or off. | `filter.use` |
| `/globalfilter <true/false>` | Toggle the filter for the entire server. | `filter.admin` |

## 📁 File Structure

* **wordlist.txt**: The cached list of censored words.
* **safewords.txt**: A customizable whitelist to prevent "false positives."
* **players.txt**: Stores the UUIDs of players who have enabled the filter.
* **config.yml**: Stores global state and admin settings.

## ⚙️ Installation

1.  Drop the `CoffeeChatFilter.jar` into your `/plugins/` folder.
2.  Restart your server.
3.  (Optional) Edit `safewords.txt` to add any specific words you want the filter to ignore.

---
*Developed with ❤️ by Coffee1307*
