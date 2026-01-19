# Smart Cooking Assistant – MVP

## Project Overview

**Smart Cooking Assistant** is a mobile application designed to help users decide what to cook using the ingredients they already have at home, while also managing their fridge and pantry digitally using AI.

Instead of searching randomly for recipes or forgetting what ingredients are available, users maintain a virtual fridge that tracks all food items, quantities, and expiration dates. The app then suggests suitable recipes, guides the user through cooking, and generates smart shopping lists when ingredients are missing.

The app focuses on three main goals:
- Reduce food waste by tracking expiration dates
- Save time when deciding what to cook
- Avoid buying duplicate groceries

The MVP delivers a simple but powerful experience:
**From fridge → to recipe → to guided cooking → to smart shopping.**

---

## Core MVP Features

### 1. Digital Fridge & Pantry (Main Feature)

The app contains a dedicated **Fridge** tab that acts as a digital inventory of all ingredients the user owns.

Each ingredient item contains:
- Name
- Quantity (optional)
- Unit (optional: grams, pieces, bottles, etc.)
- Expiration date (optional)
- Category (vegetables, dairy, meat, etc.)

This fridge becomes the main data source for recipe recommendations and shopping suggestions.

---

### 2. AI Photo Scan – Add Ingredients from Image

Users can add ingredients by taking a photo of their fridge, grocery bag, or kitchen counter.

Flow:
1. User taps "Scan Fridge"
2. Takes a photo
3. Image is sent to AI for analysis
4. AI detects all visible food items
5. A review list appears where the user can:
   - Edit ingredient names
   - Adjust quantities
   - Add or edit expiration dates
   - Delete incorrect detections

When saving:
- If an ingredient with the same name already exists, the app updates the quantity instead of creating duplicates
- Otherwise, a new ingredient is added

This feature differentiates the product by combining computer vision with inventory management.

---

### 3. Manual Ingredient Management

Users can manually add ingredients by entering:
- Ingredient name
- Quantity (optional)
- Expiration date (optional)

Users can:
- Edit ingredients at any time
- Update quantities
- Remove consumed or expired items

---

### 4. Expiration Tracking & Smart Usage

The fridge system:
- Highlights ingredients close to expiration
- Prioritizes recipes that use expiring items first

Optional future feature:
- Push notifications for soon-to-expire items

---

### 5. Ingredient-Based Recipe Search

Recipes are suggested using real fridge data instead of manual selection.

The app provides:
- Automatic ingredient matching
- Match percentage per recipe
- Filters by:
  - Best match
  - Fewest missing ingredients
  - Fastest recipes
  - Uses expiring ingredients

---

### 6. Recipe Results List

Each recipe card shows:
- Recipe image
- Recipe name
- Cooking time
- Difficulty level
- Ingredient match percentage
- Missing ingredients indicator

---

### 7. Recipe Details Screen

Displays:
- Large recipe image and title
- Full ingredient list
  - Available ingredients marked clearly
  - Missing ingredients highlighted
- Serving size selector (1 / 2 / 4 people)
- Cooking time and difficulty

Actions:
- Start Cooking Mode
- Add missing ingredients to Shopping List

---

### 8. Guided Cooking Mode

A distraction-free step-by-step cooking experience:

Features:
- One instruction per screen
- Step counter
- Next / Previous buttons
- Built-in timers for cooking steps

This mode helps beginners cook confidently without leaving the app.

---

### 9. Smart Shopping List

Automatically generated from:
- Missing ingredients in selected recipes
- Low-quantity items in the fridge

Features:
- Group ingredients by category
- Editable checklist
- Share or export list

---

### 10. Monetization & Subscription (RevenueCat)

**Free Plan**
- Manual ingredient entry
- Limited fridge size
- Limited daily recipe searches
- No AI photo scanning

**Premium Plan**
- Unlimited fridge storage
- AI photo scanning
- Expiration tracking
- Unlimited recipe searches
- Guided cooking mode
- Smart shopping lists

---

## App Navigation & Screens

### Tab 1 – Home / Cook

- Quick recipe suggestions from fridge
- "Find recipes from my fridge" button
- Daily personalized recipe ideas

---

### Tab 2 – Fridge (Main Tab)

Purpose: Manage all owned ingredients

Features:
- Ingredient list with name, quantity, expiration indicator
- Add manually button
- Scan with camera button

**Camera Scan Flow:**
1. Take photo
2. AI detects ingredients
3. Review & edit list
4. Save and merge duplicates

---

### Tab 3 – Recipe Results

- Filtered recipe list based on fridge contents

---

### Tab 4 – Recipe Details

- Ingredient breakdown
- Cooking time & difficulty
- Start Cooking Mode

---

### Tab 5 – Cooking Mode

- Step-by-step guided cooking
- Timers and navigation

---

### Tab 6 – Shopping List

- Auto-generated ingredient list
- Editable checklist

---

### Tab 7 – Profile / Subscription

- Upgrade to premium
- Manage subscription

---

## MVP Value Proposition

This MVP is a **Smart Kitchen Assistant** that:

- Digitizes the user’s fridge
- Uses AI to recognize ingredients from photos
- Prevents duplicate grocery purchases
- Tracks expiration dates to reduce food waste
- Suggests recipes based on real data
- Guides users through cooking
- Generates actionable shopping lists

This combination of AI, inventory management, cooking guidance, and monetization readiness creates a strong, innovative MVP suitable for hackathons, startup incubation, and real-world scaling.

---

## Future Extensions (Post-MVP)

- Barcode scanning
- Nutrition tracking
- Meal planning calendar
- Grocery store integrations
- Voice assistant cooking mode
- Household shared fridge

---

**End of MVP Specification**