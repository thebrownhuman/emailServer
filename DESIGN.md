# Design System: AtlasID
**Project ID:** 7528742426841309900
**Stitch Theme:** Dark mode · Manrope · 8px roundness · `#2e77ff` accent · Saturation ×2

---

## 1. Visual Theme & Atmosphere

AtlasID radiates **premium, high-trust surveillance-grade confidence**. The aesthetic is **Deep Space Fintech** — a cinematic dark foundation overlaid with luminous electric-blue accents and frosted glass panels that feel like encrypted vaults. The density is purposefully controlled: generous whitespace paired with rich information cards preventing the UI from ever feeling empty or cheap. Every screen communicates institutional authority — the kind of digital environment where a Fortune 500 company or global bank would feel proud to place their identity infrastructure.

The atmosphere can be summarized as: **Authoritative, Luminous, Secure, and Global**.

---

## 2. Color Palette & Roles

### Primary Brand Color
| Name | Hex | Role |
|---|---|---|
| **Atlas Blue** | `#2e77ff` | Primary call-to-action, active states, highlights, glow source |
| **Atlas Blue Dim** | `#1a5fe0` | Pressed/hover state of primary buttons |
| **Atlas Blue Ghost** | `rgba(46, 119, 255, 0.15)` | Tinted backgrounds on active nav items, subtle fills |
| **Atlas Blue Glow** | `rgba(46, 119, 255, 0.35)` | Box-shadow glow on focused inputs and primary buttons |

### Background & Surface Hierarchy
| Name | Hex | Role |
|---|---|---|
| **Void Black** | `#060810` | Absolute page canvas / outermost background layer |
| **Deep Navy** | `#0d1117` | Primary surface / app shell background |
| **Graphite Navy** | `#161b26` | Card and panel backgrounds (first elevation) |
| **Slate Navy** | `#1e2535` | Elevated cards, modals, form containers (second elevation) |
| **Glass White** | `rgba(255, 255, 255, 0.06)` | Glass surface base fill |
| **Glass Border** | `rgba(255, 255, 255, 0.10)` | Glass surface border / divider stroke |

### Semantic Status Colors
| Name | Hex | Role |
|---|---|---|
| **Verification Green** | `#22c55e` | Success states, verified badges, confirmed identity |
| **Alert Amber** | `#f59e0b` | Pending states, warnings, in-progress verification |
| **Threat Red** | `#ef4444` | Error states, suspicious activity, failed verification |
| **Neutral Ice** | `rgba(255, 255, 255, 0.45)` | Muted body text, placeholder text, secondary labels |

### Text Colors
| Name | Hex | Role |
|---|---|---|
| **Crisp White** | `#ffffff` | Headings, primary body text |
| **Frost White** | `rgba(255, 255, 255, 0.75)` | Secondary body text, card subtitles |
| **Muted Slate** | `rgba(255, 255, 255, 0.45)` | Tertiary text, hints, captions |
| **Link Blue** | `#5b9aff` | Inline hyperlinks, "create account", "forgot password" |

---

## 3. Typography Scale

**Font Family:** Manrope (Google Fonts) — a geometric sans-serif with technical precision and refined warmth. Used exclusively across all weights and sizes.

**Weight Usage Philosophy:** Bold for hierarchy-establishing headings that demand immediate authority; Medium for interactive labels and body that must be readable under cognitive load; Regular for supporting text that should recede gracefully.

### Typographic Hierarchy

| Name | Size | Weight | Letter-Spacing | Use Case |
|---|---|---|---|---|
| **Display H1** | 48px / 3rem | Bold (700) | −0.02em (tight) | Hero headings, splash screens |
| **Display H2** | 36px / 2.25rem | Bold (700) | −0.01em | Section titles, modal headers |
| **Display H3** | 24px / 1.5rem | SemiBold (600) | −0.01em | Card headings, step titles |
| **Body Large** | 18px / 1.125rem | Medium (500) | 0em | Primary body, feature descriptions |
| **Body Regular** | 16px / 1rem | Regular (400) | 0em | Form labels, standard paragraphs |
| **Body Small** | 14px / 0.875rem | Regular (400) | +0.01em (airy) | Supporting copy, legal text, captions |
| **Label** | 12px / 0.75rem | SemiBold (600) | +0.08em (very airy) | Status tags, badges, overlines |
| **Monospaced Code** | 14px / 0.875rem | Medium (500) | +0.04em | ID numbers, OTP digits, verification codes |

**Line Height:** Headings use 1.2× · Body text uses 1.6× · Labels use 1.0×

---

## 4. Spacing System (8pt Grid)

All spacing follows a strict **8-point base grid**. Every margin, padding, gap, and dimension is a multiple of 8px. Half-steps (4px) are permitted only for micro-adjustments within components.

| Token | Value | Use Case |
|---|---|---|
| `--space-1` | 4px | Icon-to-label nudge, micro-gap |
| `--space-2` | 8px | Tight inner padding (badges, tags) |
| `--space-3` | 12px | Input internal padding (vertical) |
| `--space-4` | 16px | Standard component padding, card inner gaps |
| `--space-5` | 20px | Slightly generous inner padding |
| `--space-6` | 24px | Card padding, section inner whitespace |
| `--space-8` | 32px | Panel-to-panel gaps, form group spacing |
| `--space-10` | 40px | Section vertical rhythm |
| `--space-12` | 48px | Generous section breathing room |
| `--space-16` | 64px | Major section breaks on landing pages |
| `--space-20` | 80px | Hero vertical padding |
| `--space-24` | 96px | Full-bleed section separators |

---

## 5. Grid Rules

### Desktop (1280px canvas)
- **Columns:** 12-column grid
- **Gutter:** 24px between columns
- **Margin:** 80px horizontal side margins
- **Max content width:** 1120px centered

### Tablet (768px–1024px)
- **Columns:** 8-column grid
- **Gutter:** 20px
- **Margin:** 40px horizontal side margins

### Mobile (< 768px)
- **Columns:** 4-column grid
- **Gutter:** 16px
- **Margin:** 20px horizontal side margins

### Layout Patterns
- **Auth screens:** Two-column split — decorative/branding left panel + form right panel (desktop); single-column stacked (mobile)
- **Dashboard:** Fixed left sidebar (240px) + fluid main content area
- **Form cards:** Centered single-column, max-width 480px, with generous padding

---

## 6. Glass Surface Styles

Glass is AtlasID's primary design signature — communicating transparent layers of security stacked protectively. All glass surfaces live on the **Deep Navy** (`#0d1117`) or darker backgrounds.

### Glass Surface Levels

**Level 1 — Frosted Panel (Identity Verified Card)**
```
background: rgba(255, 255, 255, 0.06)
backdrop-filter: blur(12px)
border: 1px solid rgba(255, 255, 255, 0.10)
border-radius: 16px
```
*Used for: main content cards, dashboard panels, feature highlight boxes*

**Level 2 — Deep Glass (Modal / Form Container)**
```
background: rgba(14, 20, 36, 0.85)
backdrop-filter: blur(20px)
border: 1px solid rgba(255, 255, 255, 0.08)
border-radius: 16px
box-shadow: 0 24px 64px rgba(0, 0, 0, 0.6)
```
*Used for: login/signup form containers, OTP dialogs, security question panels*

**Level 3 — Ultra-Dark Glass (Sidebar / Navigation)**
```
background: rgba(6, 8, 16, 0.90)
backdrop-filter: blur(8px)
border-right: 1px solid rgba(255, 255, 255, 0.06)
```
*Used for: sidebar navigation, overlay menus*

**Glass Text Treatment Inside Surfaces:**
- Heading text: `#ffffff` (full opacity — must punch through blur)
- Supporting text: `rgba(255, 255, 255, 0.70)`
- Metadata / timestamps: `rgba(255, 255, 255, 0.45)`

---

## 7. Shadow & Glow System

Shadows define elevation; glows define interactivity and brand presence.

### Elevation Shadows
| Level | CSS | Used On |
|---|---|---|
| **Elevation 0** | `none` | Flat tags, inline elements |
| **Elevation 1** | `0 2px 8px rgba(0,0,0,0.3)` | Subtle card lift |
| **Elevation 2** | `0 8px 24px rgba(0,0,0,0.4)` | Form cards, dashboard panels |
| **Elevation 3** | `0 24px 64px rgba(0,0,0,0.6)` | Modals, drawers, full-page overlays |

### Glow System (Brand Presence)
| Name | CSS | Used On |
|---|---|---|
| **Atlas Blue Glow — Soft** | `0 0 0 3px rgba(46,119,255,0.25)` | Input focus ring |
| **Atlas Blue Glow — Medium** | `0 4px 24px rgba(46,119,255,0.40)` | Primary button hover/focus |
| **Atlas Blue Glow — Hero** | `0 0 80px rgba(46,119,255,0.20)` | Hero backgrounds, ambient light wash |
| **Success Glow** | `0 4px 16px rgba(34,197,94,0.30)` | Verified state badge pulse |
| **Error Glow** | `0 4px 16px rgba(239,68,68,0.30)` | Error states, threat detection alerts |

### Decorative Background Glow
The hero canvas features ambient radial glows applied as pseudo-elements:
```css
/* Left hero glow — primary brand */
background: radial-gradient(ellipse 600px 400px at 20% 50%, rgba(46,119,255,0.15), transparent);

/* Right glow — violet accent */
background: radial-gradient(ellipse 400px 300px at 80% 30%, rgba(124,58,237,0.10), transparent);
```

---

## 8. Form Controls

All form controls communicate precision and security — they must feel like high-security terminal interfaces, not casual consumer forms.

### Text Input (Standard)
```
background: rgba(255, 255, 255, 0.05)
border: 1px solid rgba(255, 255, 255, 0.12)
border-radius: 8px
padding: 14px 16px
color: #ffffff
font: Manrope 16px Regular
transition: border-color 200ms ease, box-shadow 200ms ease
```

**Focus state:**
```
border-color: #2e77ff
box-shadow: 0 0 0 3px rgba(46, 119, 255, 0.25)
outline: none
```

**Error state:**
```
border-color: #ef4444
box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.20)
```

**Placeholder text:** `rgba(255, 255, 255, 0.35)` — visibly receded, never competing with live input

### OTP / Code Input
Six standalone digit boxes arranged in a horizontal row with 8px gaps:
```
width: 52px
height: 64px
background: rgba(255, 255, 255, 0.06)
border: 1.5px solid rgba(255, 255, 255, 0.14)
border-radius: 8px
font: Manrope 24px Bold, monospaced
text-align: center
color: #ffffff
```
**Filled digit style:** `border-color: #2e77ff` + subtle blue glow

### Dropdown / Select
Same base styling as text input. The dropdown chevron icon uses `rgba(255,255,255,0.45)`. Open state reveals a **glass-surface panel** (Level 1) with options at 44px touch-target height.

### Form Field Label
```
font: Manrope 12px SemiBold
color: rgba(255, 255, 255, 0.65)
text-transform: uppercase
letter-spacing: 0.08em
margin-bottom: 8px
```

### Form Group Spacing
- Vertical gap between form fields: **24px**
- Label to input vertical gap: **8px**
- Helper text below input: **6px**, Manrope 12px, `rgba(255,255,255,0.45)`

### Checkbox / Toggle
Not flat native checkboxes — always a custom-styled component:
```
width: 18px; height: 18px
border: 1.5px solid rgba(255, 255, 255, 0.25)
border-radius: 3px
checked background: #2e77ff with white checkmark SVG
```

### Step Progress Indicator
Multi-step forms (e.g., Sign-up) display a progress indicator at top:
- Active step: filled `#2e77ff` circle + bold label
- Completed step: check icon in `#22c55e` circle
- Upcoming step: empty circle `rgba(255,255,255,0.20)` + muted label

---

## 9. Button Variants

Buttons use **8px border-radius** consistently across the system (matching the project's global `ROUND_EIGHT` setting). All buttons use Manrope SemiBold 15px–16px and have minimum height of 48px.

### Primary Button (Atlas Blue Fill)
```
background: #2e77ff
color: #ffffff
border-radius: 8px
padding: 14px 28px
font: Manrope 16px SemiBold
border: none
box-shadow: 0 4px 16px rgba(46, 119, 255, 0.30)
transition: all 200ms ease
```
**Hover:** `background: #1a5fe0` + intensified glow `0 4px 24px rgba(46,119,255,0.50)`
**Active/Pressed:** `background: #1451cc` + scale `0.98`
**Disabled:** `background: rgba(46,119,255,0.30)`, `cursor: not-allowed`

*Used for: "Create Account", "Verify OTP", "Continue", "Save Changes"*

### Secondary Button (Ghost / Outlined)
```
background: transparent
color: #2e77ff
border: 1.5px solid rgba(46, 119, 255, 0.50)
border-radius: 8px
padding: 14px 28px
font: Manrope 16px SemiBold
transition: all 200ms ease
```
**Hover:** `background: rgba(46,119,255,0.10)` + `border-color: #2e77ff`

*Used for: "Sign In", "Back", "Cancel", secondary navigation actions*

### Tertiary Button (Text / Flat)
```
background: transparent
color: rgba(255, 255, 255, 0.65)
border: none
padding: 8px 12px
font: Manrope 15px Medium
text-decoration: none
```
**Hover:** `color: #ffffff` + underscore decorative line

*Used for: "Resend Code", "Need help signing in?", "Skip for now"*

### Destructive Button (Threat Red)
```
background: rgba(239, 68, 68, 0.15)
color: #ef4444
border: 1px solid rgba(239, 68, 68, 0.30)
border-radius: 8px
padding: 14px 28px
```
**Hover:** `background: rgba(239,68,68,0.25)`

*Used for: "Remove Access", "Revoke Device", "Report Suspicious Activity"*

### Icon Button (Compact)
```
width: 40px; height: 40px
background: rgba(255, 255, 255, 0.06)
border: 1px solid rgba(255, 255, 255, 0.10)
border-radius: 8px
display: grid; place-items: center
color: rgba(255, 255, 255, 0.70)
transition: all 150ms ease
```
**Hover:** `background: rgba(255,255,255,0.12)` + `color: #ffffff`

*Used for: navigation back arrows, copy-to-clipboard, settings gear, dismiss*

### Nav Link (Header / Sidebar)
```
color: rgba(255, 255, 255, 0.65)
font: Manrope 15px Medium
padding: 8px 16px
border-radius: 8px
transition: all 150ms ease
```
**Hover:** `color: #ffffff` + `background: rgba(255,255,255,0.06)`
**Active:** `color: #2e77ff` + `background: rgba(46,119,255,0.10)` + left border `3px solid #2e77ff` (sidebar only)

---

## 10. Component Patterns

### Verification Badge
Pill-shaped badge with icon:
```
background: rgba(34, 197, 94, 0.15)
border: 1px solid rgba(34, 197, 94, 0.30)
border-radius: 999px (pill)
color: #22c55e
padding: 4px 12px
font: Manrope 12px SemiBold
letter-spacing: 0.06em
```
Variants: Success (green), Pending (amber), Threat (red), Neutral (white/muted)

### Identity Card Component
Glass Level 1 surface with:
- Header row: Avatar (40px circle) + Name (Bold 16px) + Badge
- ID Number: Monospaced 18px Bold, letter-spacing 0.08em
- Footer row: Expiry date + card type in Label style

### Activity Feed Row
```
height: 56px minimum
padding: 0 16px
display: flex; align-items: center; gap: 12px
border-bottom: 1px solid rgba(255,255,255,0.06)
```
- Icon: 36px circle with light-tinted background matching status color
- Primary text: 14px Medium White
- Metadata: 12px Regular Muted Slate

### Linked Service Row
Similar to Activity Feed but includes a "Connected" status badge and a right chevron/arrow. Connected services show a green dot pulse animation (2s infinite, opacity 0→1→0).

---

## 11. Prompting Primer for Stitch

When generating new AtlasID screens, always include this preamble in prompts:

> *"Design in the AtlasID visual language: deep space dark UI with `#060810` as the canvas, `#2e77ff` as the electric blue accent, Manrope typeface across all weights, glass surfaces with 12px backdrop blur and `rgba(255,255,255,0.06)` fill, 8px border-radius on all interactive controls, 8pt spacing grid. The atmosphere is premium fintech — authoritative, luminous, globally trusted."*

---

*Extracted from 18 screens across the AtlasID Stitch project (ID: `7528742426841309900`).*
*Source screens include: Design System Kit · Landing & Login · Sign-up Personal Info · OTP Verification · Security Questions · User Dashboard · All device breakpoints (mobile, tablet, desktop).*
