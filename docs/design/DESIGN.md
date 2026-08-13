---
name: Literary Intelligence
colors:
  surface: '#fdf8fd'
  surface-dim: '#ddd9de'
  surface-bright: '#fdf8fd'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f7f2f8'
  surface-container: '#f1ecf2'
  surface-container-high: '#ebe7ec'
  surface-container-highest: '#e5e1e7'
  on-surface: '#1c1b1f'
  on-surface-variant: '#464652'
  inverse-surface: '#313034'
  inverse-on-surface: '#f4eff5'
  outline: '#777683'
  outline-variant: '#c7c5d4'
  surface-tint: '#4f54b4'
  primary: '#15157d'
  on-primary: '#ffffff'
  primary-container: '#2e3192'
  on-primary-container: '#9da1ff'
  inverse-primary: '#c0c1ff'
  secondary: '#5e5e5e'
  on-secondary: '#ffffff'
  secondary-container: '#e4e2e2'
  on-secondary-container: '#656464'
  tertiary: '#252831'
  on-tertiary: '#ffffff'
  tertiary-container: '#3b3e47'
  on-tertiary-container: '#a6a9b4'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#e1e0ff'
  primary-fixed-dim: '#c0c1ff'
  on-primary-fixed: '#04006d'
  on-primary-fixed-variant: '#373a9b'
  secondary-fixed: '#e4e2e2'
  secondary-fixed-dim: '#c8c6c6'
  on-secondary-fixed: '#1b1c1c'
  on-secondary-fixed-variant: '#474747'
  tertiary-fixed: '#e0e2ee'
  tertiary-fixed-dim: '#c4c6d2'
  on-tertiary-fixed: '#181b24'
  on-tertiary-fixed-variant: '#434750'
  background: '#fdf8fd'
  on-background: '#1c1b1f'
  surface-variant: '#e5e1e7'
typography:
  display-quote:
    fontFamily: Playfair Display
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 42px
    letterSpacing: -0.02em
  display-quote-mobile:
    fontFamily: Playfair Display
    fontSize: 24px
    fontWeight: '700'
    lineHeight: 32px
    letterSpacing: -0.01em
  headline-md:
    fontFamily: Inter
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
  body-lg:
    fontFamily: Inter
    fontSize: 18px
    fontWeight: '400'
    lineHeight: 28px
  body-md:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  label-lg:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '500'
    lineHeight: 20px
    letterSpacing: 0.1px
  label-sm:
    fontFamily: Inter
    fontSize: 11px
    fontWeight: '500'
    lineHeight: 16px
    letterSpacing: 0.5px
rounded:
  sm: 0.125rem
  DEFAULT: 0.25rem
  md: 0.375rem
  lg: 0.5rem
  xl: 0.75rem
  full: 9999px
spacing:
  margin-mobile: 1.25rem
  margin-tablet: 2.5rem
  gutter: 1rem
  stack-sm: 0.5rem
  stack-md: 1rem
  stack-lg: 2rem
---

## Brand & Style

The design system is built for a premium, editorial trivia experience that treats quotes as artifacts of wisdom rather than mere data points. The brand personality is intellectual, sophisticated, and clean, targeting an audience that appreciates literature, history, and modern aesthetics.

The visual style blends **Minimalism** with **Modern Editorial** influences. It utilizes generous white space, a high-contrast typographic hierarchy, and a structured layout to focus entirely on readability and the "weight" of the written word. While inspired by Material 3's functional logic, the aesthetic is elevated through sophisticated serif flourishes and a restrained color palette to evoke the feeling of a digital broadsheet or a high-end literary journal.

## Colors

The color palette is grounded in **Deep Indigo** as the primary driver for interactive elements and brand recognition. This is paired with a sophisticated **Slate** for secondary information.

- **Primary (Deep Indigo):** Used for key actions, progress indicators, and active states.
- **Neutral (Black/Slate):** Used for maximum legibility of long-form text and quotes against a clean off-white background.
- **Error (Pure Red):** Reserved strictly for incorrect answers or critical system alerts to ensure high visibility and immediate feedback.
- **Surface:** Utilizes subtle tonal shifts in the tertiary range to separate the "Stage" (the quote card) from the "Controls" (the UI).

## Typography

This design system uses a high-contrast pairing to distinguish between content and interface.

- **The Quote (Playfair Display):** All famous quotes must be set in this expressive serif. It provides the "voice" of the author. On mobile, the size scales down to maintain the line count and prevent excessive scrolling.
- **The Interface (Inter):** All navigation, labels, buttons, and instructional text use Inter. It provides a neutral, highly legible framework that stays out of the way of the primary content.
- **Hierarchy:** Use `display-quote` for the central quiz question and `headline-md` for category titles or score results.

## Layout & Spacing

The design system follows a **fluid grid** model optimized for vertical reading.

- **Portrait (Mobile):** A 4-column grid with 20px (1.25rem) side margins. Components are primarily stacked vertically to prioritize the quote's horizontal space.
- **Landscape (Mobile):** Transitions to a 2-column split view. The left side anchors the quote in a fixed container; the right side contains the scrollable answer choices or keyboard input.
- **Spacing Rhythm:** Use a base-4 system. Elements within a component (e.g., a label and an input) use `stack-sm`. Distinct sections or components use `stack-lg`.

## Elevation & Depth

This design system utilizes **Tonal Layers** rather than heavy shadows to maintain its editorial, clean feel.

1. **Level 0 (Background):** The base canvas (`#FFFBFE`).
2. **Level 1 (Cards/Quotes):** Quote containers use a subtle tonal shift or a very fine 1px border (`#E0E2EE`) rather than a shadow. This keeps the interface feeling "flat" and printed.
3. **Level 2 (Modals/Overlays):** For dialogs and bottom sheets, use a soft, extra-diffused shadow (15% opacity Primary color) to provide a clear separation from the quiz content.
4. **Interaction:** Buttons do not elevate on tap; instead, they use a fill-color transition or a subtle scale-down effect (98%) to simulate a physical press.

## Shapes

The design system adopts a **Soft** shape language. While the brand is intellectual, strictly sharp corners feel overly clinical.

- **Cards & Inputs:** Use 0.25rem (`rounded-sm`) for a precise, tailored look.
- **Buttons:** Use 0.5rem (`rounded-lg`) to distinguish interactive elements from static containers.
- **Progress Bars:** Use fully rounded ends (caps) to provide a visual break from the rectangular grid of the rest of the UI.

## Components

### Buttons
- **Filled (Primary):** Background: Primary Indigo; Text: White. Used for the "Submit" or "Continue" actions.
- **Outlined (Secondary):** 1px border of Slate; Text: Slate. Used for optional actions like "Hint" or "Skip."

### Cards (The Quote Stage)
The central component of the app. It should feature a large opening quotation mark in a light tint of the Primary color as a background element. The text is centered or left-aligned depending on length, using the `display-quote` type level.

### Text Fields
Following Material 3 "Outlined" style.
- **Default:** 1px Slate border with floating label.
- **Error State:** Border becomes 2px `#FF0000`. Supporting text (error message) appears below in `#FF0000` using `label-sm`.

### Progress Bars
A thin, 4px track spanning the full width of the top margin. The track is Tertiary Indigo, and the fill is Primary Indigo. This provides a constant, non-intrusive sense of "completion."

### Selection Controls
- **Chips:** Used for category selection. When unselected, they are outlined. When selected, they take on a Primary Tonal fill (light indigo) with a check icon.
- **Lists:** Result lists use a thin 1px horizontal divider between items to maintain the editorial grid.

### Feedback Modals
Appear as Bottom Sheets on mobile. They use a backdrop blur to keep the user focused on the quiz state while providing instant feedback on their answer.