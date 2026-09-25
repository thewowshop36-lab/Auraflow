# AuraFlow Mobile Web - Vercel Deployment & Google AdSense Guide
# ویریسل (Vercel) پر موبائل ویب سائٹ ڈپلائے کرنے اور گوگل ایڈسینس لگانے کی مکمل گائیڈ

یہ موبائل ویب سائٹ خصوصی طور پر **Vercel** پر ہوسٹ ہونے کے لیے ڈیزائن کی گئی ہے۔ اس میں گوگل ایڈسینس، پریمیم وی آئی پی کوڈ پیجز، لائیو ارننگ کاؤنٹر، اور پی ڈبلیو اے (PWA) فیچرز پہلے سے کنفیگر ہیں۔

---

## 🚀 ویریسل پر ڈپلائے کرنے کے 3 آسان طریقے (3 Easy Ways to Deploy on Vercel)

### طریقہ 1: AI Studio سے ایکسپورٹ کر کے (سب سے آسان)
1. اوپر دائیں کونے میں **Settings** مینو کھولیں اور **"Export ZIP"** پر کلک کریں۔
2. زپ فائل کو ان زپ (Unzip) کریں۔
3. [Vercel](https://vercel.com/) پر جائیں، لاگ ان کریں۔
4. **"Add New..."** -> **"Project"** منتخب کریں۔
5. اگر آپ نے کوڈ کو GitHub پر رکھا ہے تو امپورٹ کریں، یا Vercel CLI کے ذریعے اپ لوڈ کریں:
   ```bash
   npm i -g vercel
   vercel --prod
   ```
6. ویریسل خود بخود `vercel.json` اور `public/index.html` کو ڈیٹیکٹ کر کے 30 سیکنڈ میں لائیو کر دے گا!

---

### طریقہ 2: براہ راست GitHub سے کنیکٹ کر کے (One-Click Auto Deploy)
1. AI Studio سے پراجیکٹ کو اپنے **GitHub** اکاؤنٹ میں پش کریں۔
2. [Vercel Dashboard](https://vercel.com/dashboard) پر جائیں۔
3. **"Import Git Repository"** پر کلک کریں۔
4. Framework Preset میں **"Other"** رہنے دیں۔
5. **Root Directory**: `./` یا `./public`
6. **"Deploy"** بٹن پر کلک کریں۔ آپ کی موبائل ویب سائٹ لائیو ہو جائے گی:
   `https://auraflow-yourname.vercel.app`

---

## 💰 گوگل ایڈسینس (Google AdSense) کنفیگریشن

### 1. `ads.txt` فائل
گوگل ایڈسینس کی پالیسی کے تحت `ads.txt` فائل ویب سائٹ کے روٹ پر ہونا لازمی ہے۔ ہم نے یہ فائل پہلے سے بنا کر رکھ دی ہے:
- پاتھ: `/ads.txt` اور `/public/ads.txt`
- اس میں اپنی اصلی پبلشر آئی ڈی درج کریں:
  ```
  google.com, pub-XXXXXXXXXXXXXXXX, DIRECT, f08c47fec0942fa0
  ```

### 2. AdSense Script Tag
ویب سائٹ میں `public/index.html` کے `<head>` سیکشن میں ایڈسینس کا ٹیگ شامل ہے:
```html
<script async src="https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js?client=ca-pub-XXXXXXXXXXXXXXXX" crossorigin="anonymous"></script>
```
آپ اپنی اصلی پبلشر آئی ڈی (`ca-pub-XXXXXXXX`) سے اسے تبدیل کر سکتے ہیں، یا ویب سائٹ کے اندر ایڈسینس سیٹنگز ٹیب سے لائیو تبدیل کر سکتے ہیں۔

### 3. سپورٹ شدہ ایڈ یونٹس (Supported Ad Units)
1. **In-Feed Native Banner (300x250)**: پیج کے درمیان نظر آتا ہے۔
2. **Sticky Bottom Anchor Ad (320x50)**: موبائل اسکرین کے نچلے حصے پر مستقل رہتا ہے۔
3. **Interstitial Full-Screen Ad**: یوزر جب کوئی ایکشن لیتا ہے یا نیا پیج کھولتا ہے تو 5 سیکنڈ کے لیے پاپ اپ ایڈ کھلتا ہے۔
4. **Rewarded Ad**: یوزر پریمیم کوڈ انلاک کرنے کے لیے ویڈیو ایڈ دیکھتا ہے جس سے زیادہ ارننگ ہوتی ہے۔

---

## 💎 پریمیم کوڈ پیجز (VIP Source Code Pages)
اس موبائل ویب سائٹ میں 5 پریمیم کوڈ ٹیمپلیٹس شامل ہیں:
1. Google Ads + Next.js High-CTR Landing Page
2. Python Multi-Threaded Business Lead Scraper
3. Vercel Serverless Ads & Webhook Monetization API
4. WhatsApp / Telegram AI Alert Bot
5. In-App Purchase & Stripe Paywall

**ڈیمو لائسنس کیز (Demo License Keys to Unlock):**
- `AURA-VIP-2026`
- `VERCEL-PRO-2026`
- `EARN-ADS-100`
