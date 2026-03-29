// visuals.js — four visual components for Robotegers ITS
// Each returns an HTML string or mutates a container element.

// ══════════════════════════════════════════════════════════════════════════════
// 1. INTERACTIVE NUMBER LINE
// ══════════════════════════════════════════════════════════════════════════════
function renderNumberLine(container, options = {}) {
  const {
    min = -10, max = 10,
    highlight = [],          // [{val, color, label}]
    animate = null,          // {from, to, color} — animated arrow
    showAbsolute = false,
  } = options;

  const W = 560, H = 120, PAD = 40;
  const range = max - min;
  const toX = v => PAD + ((v - min) / range) * (W - 2 * PAD);
  const ZERO_X = toX(0);

  let svg = `<svg viewBox="0 0 ${W} ${H}" xmlns="http://www.w3.org/2000/svg"
    style="width:100%;max-width:${W}px;display:block;margin:0 auto">
  <defs>
    <marker id="ah" markerWidth="8" markerHeight="6" refX="8" refY="3" orient="auto">
      <polygon points="0 0,8 3,0 6" fill="#8B1A1A"/>
    </marker>
    <marker id="ah2" markerWidth="8" markerHeight="6" refX="8" refY="3" orient="auto">
      <polygon points="0 0,8 3,0 6" fill="#2E7D4F"/>
    </marker>
  </defs>`;

  // Axis line
  svg += `<line x1="${PAD-10}" y1="60" x2="${W-PAD+10}" y2="60"
    stroke="#8B1A1A" stroke-width="2" marker-end="url(#ah)"/>`;
  // Left arrow
  svg += `<line x1="${PAD+10}" y1="60" x2="${PAD-8}" y2="60"
    stroke="#8B1A1A" stroke-width="2" marker-start="url(#ah2)"/>`;

  // Tick marks and labels
  for (let v = min; v <= max; v++) {
    const x = toX(v);
    const isMajor = v % 5 === 0 || v === 0;
    svg += `<line x1="${x}" y1="${60 - (isMajor?10:6)}" x2="${x}" y2="${60+(isMajor?10:6)}"
      stroke="${v===0?'#8B1A1A':'#999'}" stroke-width="${v===0?2:1}"/>`;
    if (isMajor || (range <= 12))
      svg += `<text x="${x}" y="88" text-anchor="middle" font-size="11"
        fill="${v===0?'#8B1A1A':'#555'}" font-family="DM Mono,monospace"
        font-weight="${v===0?'700':'400'}">${v}</text>`;
  }

  // Zero label
  svg += `<text x="${ZERO_X}" y="48" text-anchor="middle" font-size="10"
    fill="#8B1A1A" font-family="DM Sans,sans-serif" font-weight="600">0</text>`;

  // Highlights (dots + labels)
  for (const h of highlight) {
    const x = toX(h.val);
    const col = h.color || '#E8A020';
    svg += `<circle cx="${x}" cy="60" r="7" fill="${col}" stroke="white" stroke-width="2"/>`;
    if (h.label)
      svg += `<text x="${x}" y="35" text-anchor="middle" font-size="11"
        fill="${col}" font-weight="700" font-family="DM Sans,sans-serif">${h.label}</text>`;
  }

  // Absolute value bracket
  if (showAbsolute && highlight.length > 0) {
    const val = highlight[0].val;
    const x = toX(val);
    const bracketY = 105;
    const sign = val < 0 ? -1 : 1;
    svg += `<line x1="${ZERO_X}" y1="${bracketY}" x2="${x}" y2="${bracketY}"
      stroke="#2980B9" stroke-width="2" stroke-dasharray="4"/>`;
    svg += `<text x="${(ZERO_X+x)/2}" y="${bracketY-4}" text-anchor="middle"
      font-size="11" fill="#2980B9" font-family="DM Mono,monospace">|${val}|=${Math.abs(val)}</text>`;
  }

  // Animated arrow (uses CSS animation)
  if (animate) {
    const x1 = toX(animate.from), x2 = toX(animate.to);
    const col = animate.color || '#2E7D4F';
    const id = 'anim_' + Math.random().toString(36).slice(2);
    svg += `<line id="${id}" x1="${x1}" y1="55" x2="${x1}" y2="55"
      stroke="${col}" stroke-width="3" marker-end="url(#ah2)" opacity="0.9">
      <animate attributeName="x2" from="${x1}" to="${x2}" dur="1.2s"
        begin="0.3s" fill="freeze" calcMode="spline"
        keySplines="0.42 0 0.58 1" keyTimes="0;1"/>
    </line>`;
    svg += `<text x="${(x1+x2)/2}" y="42" text-anchor="middle" font-size="12"
      fill="${col}" font-weight="700" font-family="DM Sans,sans-serif">
      <animate attributeName="opacity" from="0" to="1" dur="0.5s" begin="1s" fill="freeze"/>
      ${animate.from} ${animate.to >= animate.from ? '→' : '←'} ${animate.to}
    </text>`;
  }

  svg += `</svg>`;
  container.innerHTML = svg;
}

// ══════════════════════════════════════════════════════════════════════════════
// 2. SIGN RULE TABLE (Multiplication & Division)
// ══════════════════════════════════════════════════════════════════════════════
function renderSignTable(container, options = {}) {
  const { operation = 'multiply', highlight = null } = options;
  // highlight: {row: 'positive'|'negative', col: 'positive'|'negative'}
  const op = operation === 'divide' ? '÷' : '×';
  const title = operation === 'divide' ? 'Division Sign Rules' : 'Multiplication Sign Rules';

  const cells = [
    ['', '× Positive', '× Negative'],
    ['Positive', '+ Positive', '− Negative'],
    ['Negative', '− Negative', '+ Positive'],
  ];
  if (operation === 'divide') {
    cells[0] = ['', '÷ Positive', '÷ Negative'];
  }

  let html = `<div style="font-family:'DM Sans',sans-serif;max-width:400px;margin:0 auto">
    <div style="font-size:.8rem;font-weight:700;letter-spacing:.08em;text-transform:uppercase;
      color:#8B1A1A;margin-bottom:.75rem;text-align:center">${title}</div>
    <table style="width:100%;border-collapse:collapse;border-radius:12px;overflow:hidden;box-shadow:0 4px 20px rgba(139,26,26,.12)">`;

  cells.forEach((row, ri) => {
    html += '<tr>';
    row.forEach((cell, ci) => {
      const isHeader = ri === 0 || ci === 0;
      const isPos = cell.includes('+ Positive');
      const isNeg = cell.includes('− Negative');
      const isHL = highlight &&
        ((ri === 1 && highlight.row === 'positive') || (ri === 2 && highlight.row === 'negative')) &&
        ((ci === 1 && highlight.col === 'positive') || (ci === 2 && highlight.col === 'negative'));

      let bg = isHeader ? '#8B1A1A' : isHL ? '#FFF3CD' : 'white';
      let color = isHeader ? 'white' : isPos ? '#2E7D4F' : isNeg ? '#C0392B' : '#333';
      let fw = isHeader || isHL ? '700' : '500';
      let fs = isHeader ? '.8rem' : '1rem';

      html += `<td style="padding:.75rem 1rem;text-align:center;border:1px solid rgba(139,26,26,.1);
        background:${bg};color:${color};font-weight:${fw};font-size:${fs};
        ${isHL ? 'box-shadow:inset 0 0 0 2px #E8A020;' : ''}">
        ${cell.replace('+ ','').replace('− ','')}
        ${isPos ? ' ✓' : ''}${isNeg ? ' ✓' : ''}
      </td>`;
    });
    html += '</tr>';
  });

  html += `</table>
    <div style="margin-top:.75rem;display:flex;gap:1rem;justify-content:center;font-size:.82rem">
      <span style="color:#2E7D4F;font-weight:600">✓ Same signs → Positive</span>
      <span style="color:#C0392B;font-weight:600">✓ Different signs → Negative</span>
    </div></div>`;

  container.innerHTML = html;
}

// ══════════════════════════════════════════════════════════════════════════════
// 3. BUILDING FLOOR DIAGRAM
// ══════════════════════════════════════════════════════════════════════════════
function renderBuilding(container, options = {}) {
  const { highlight = null, maxFloor = 5, minBasement = -3 } = options;
  // highlight: integer value to emphasise

  const floors = [];
  for (let f = maxFloor; f >= minBasement; f--) floors.push(f);

  let html = `<div style="font-family:'DM Sans',sans-serif;max-width:260px;margin:0 auto">
    <div style="font-size:.8rem;font-weight:700;letter-spacing:.08em;text-transform:uppercase;
      color:#8B1A1A;margin-bottom:.75rem;text-align:center">🏢 Building Floors</div>`;

  floors.forEach(f => {
    const isHL    = highlight === f;
    const isZero  = f === 0;
    const isNeg   = f < 0;
    const label   = f === 0 ? 'G  (Ground)' : f > 0 ? `F${f}` : `B${Math.abs(f)}`;
    const intLabel = f === 0 ? '0' : f > 0 ? `+${f}` : `${f}`;
    const bg = isHL ? '#FFF3CD' : isZero ? '#F5F0E8' : isNeg ? '#FEF5F5' : '#F0FFF4';
    const border = isHL ? '2px solid #E8A020' : isZero ? '2px solid #8B1A1A' : '1px solid #ddd';
    const color  = isNeg ? '#C0392B' : isZero ? '#8B1A1A' : '#2E7D4F';

    html += `<div style="display:flex;align-items:center;gap:.75rem;padding:.45rem .9rem;
      background:${bg};border:${border};margin-bottom:2px;border-radius:6px;
      ${isHL ? 'box-shadow:0 2px 12px rgba(232,160,32,.3);' : ''}">
      <div style="width:36px;text-align:center;font-family:'DM Mono',monospace;
        font-weight:700;font-size:1rem;color:${color}">${intLabel}</div>
      <div style="font-size:.85rem;color:#555">${label}</div>
      ${isHL ? '<div style="margin-left:auto;font-size:.8rem;font-weight:700;color:#E8A020">← HERE</div>' : ''}
      ${f === 0 ? '<div style="margin-left:auto;font-size:.75rem;color:#8B1A1A;font-weight:600">Zero</div>' : ''}
    </div>`;
  });

  html += `<div style="margin-top:.75rem;display:flex;gap:1rem;justify-content:center;font-size:.78rem">
    <span style="color:#2E7D4F;font-weight:600">↑ Positive = Above ground</span>
    <span style="color:#C0392B;font-weight:600">↓ Negative = Basement</span>
  </div></div>`;

  container.innerHTML = html;
}

// ══════════════════════════════════════════════════════════════════════════════
// 4. STEP-BY-STEP WORKED EXAMPLE ANIMATION
// ══════════════════════════════════════════════════════════════════════════════
function renderStepAnimation(container, steps, options = {}) {
  // steps: [{label: string, expression: string, highlight?: string, color?: string}]
  const { title = 'Step-by-Step' } = options;
  let currentStep = 0;

  function render() {
    let html = `<div style="font-family:'DM Sans',sans-serif;max-width:480px;margin:0 auto">
      <div style="font-size:.8rem;font-weight:700;letter-spacing:.08em;text-transform:uppercase;
        color:#8B1A1A;margin-bottom:.75rem;text-align:center">${title}</div>
      <div style="display:flex;flex-direction:column;gap:.5rem">`;

    steps.forEach((step, i) => {
      const done    = i < currentStep;
      const current = i === currentStep;
      const locked  = i > currentStep;
      const col     = step.color || (done ? '#2E7D4F' : current ? '#8B1A1A' : '#999');
      const bg      = current ? 'rgba(139,26,26,.06)' : done ? 'rgba(46,125,79,.05)' : 'transparent';

      html += `<div style="display:flex;align-items:center;gap:.75rem;padding:.65rem .9rem;
        border-radius:10px;background:${bg};border:1.5px solid ${current?'rgba(139,26,26,.2)':done?'rgba(46,125,79,.2)':'rgba(0,0,0,.06)'};
        opacity:${locked ? .35 : 1};transition:all .3s">
        <div style="width:24px;height:24px;border-radius:50%;background:${col};
          display:flex;align-items:center;justify-content:center;
          font-size:.72rem;font-weight:700;color:white;flex-shrink:0">
          ${done ? '✓' : i + 1}
        </div>
        <div style="flex:1">
          <div style="font-size:.78rem;color:#888;margin-bottom:.15rem">${step.label}</div>
          <div style="font-family:'DM Mono',monospace;font-size:1rem;font-weight:600;
            color:${col}">${step.expression}</div>
        </div>
      </div>`;
    });

    html += `</div>
      <div style="display:flex;gap:.5rem;margin-top:1rem;justify-content:center">`;

    if (currentStep > 0)
      html += `<button onclick="window._stepBack()" style="background:white;border:1.5px solid #ccc;
        border-radius:8px;padding:.45rem 1rem;font-size:.82rem;cursor:pointer;
        font-family:'DM Sans',sans-serif">← Back</button>`;

    if (currentStep < steps.length - 1)
      html += `<button onclick="window._stepForward()" style="background:#8B1A1A;color:white;border:none;
        border-radius:8px;padding:.45rem 1.2rem;font-size:.82rem;cursor:pointer;
        font-family:'DM Sans',sans-serif;font-weight:600">Next Step →</button>`;
    else
      html += `<div style="background:rgba(46,125,79,.1);border:1px solid rgba(46,125,79,.3);
        border-radius:8px;padding:.45rem 1rem;font-size:.82rem;color:#2E7D4F;font-weight:600">
        ✓ Complete!</div>`;

    html += `</div></div>`;
    container.innerHTML = html;
  }

  window._stepForward = () => { if (currentStep < steps.length - 1) { currentStep++; render(); }};
  window._stepBack    = () => { if (currentStep > 0) { currentStep--; render(); }};
  render();
}

// ══════════════════════════════════════════════════════════════════════════════
// DISPATCHER — called from main app
// ══════════════════════════════════════════════════════════════════════════════
function renderVisual(container, visualType, conceptId, hintLevel, customData) {
  if (!container) return;
  container.innerHTML = '';

  if (visualType === 'number_line' || conceptId.startsWith('m2') ||
      conceptId.startsWith('m3') || conceptId.startsWith('m4')) {

    // Build contextual options based on conceptId
    const opts = buildNumberLineOpts(conceptId, customData);
    renderNumberLine(container, opts);

  } else if (visualType === 'sign_table' ||
             conceptId.startsWith('m5') || conceptId.startsWith('m6')) {

    const op = conceptId.startsWith('m6') ? 'divide' : 'multiply';
    renderSignTable(container, { operation: op, highlight: customData?.highlight });

  } else if (visualType === 'building' || conceptId.startsWith('m1')) {
    renderBuilding(container, { highlight: customData?.highlight ?? null });

  } else {
    renderNumberLine(container, {});
  }
}

function buildNumberLineOpts(conceptId, customData) {
  const base = { min: -10, max: 10 };
  if (!customData) return base;

  // If a specific operation is passed, animate it
  if (customData.from !== undefined && customData.to !== undefined) {
    return {
      ...base,
      animate: { from: customData.from, to: customData.to, color: '#2E7D4F' },
      highlight: [
        { val: customData.from, color: '#8B1A1A', label: 'Start' },
        { val: customData.to,   color: '#2E7D4F', label: 'End' },
      ]
    };
  }
  if (customData.highlight !== undefined) {
    return {
      ...base,
      highlight: [{ val: customData.highlight, color: '#8B1A1A', label: String(customData.highlight) }],
      showAbsolute: conceptId.includes('c3'),
    };
  }
  return base;
}

// Pre-built step animations for common worked examples
const STEP_ANIMATIONS = {
  'add_diff_signs': [
    {label:'Identify signs',      expression:'−5 + 8  →  different signs'},
    {label:'Find absolute values',expression:'|−5| = 5  and  |8| = 8'},
    {label:'Compare magnitudes',  expression:'8 > 5  →  larger is positive'},
    {label:'Subtract',            expression:'8 − 5 = 3'},
    {label:'Keep dominant sign',  expression:'Answer = +3  ✓', color:'#2E7D4F'},
  ],
  'subtract_to_add': [
    {label:'Identify subtraction', expression:'5 − (−3)'},
    {label:'Rule: a − b = a+(−b)', expression:'Flip sign of second number'},
    {label:'Rewrite',              expression:'5 − (−3) = 5 + (+3)'},
    {label:'Add',                  expression:'5 + 3 = 8'},
    {label:'Answer',               expression:'= 8  ✓', color:'#2E7D4F'},
  ],
  'neg_times_neg': [
    {label:'Both numbers negative', expression:'−5 × (−4)'},
    {label:'Identify sign rule',    expression:'Same signs → Positive result'},
    {label:'Multiply magnitudes',   expression:'5 × 4 = 20'},
    {label:'Apply sign',            expression:'Result = +20'},
    {label:'Answer',                expression:'−5 × (−4) = +20  ✓', color:'#2E7D4F'},
  ],
  'division_signs': [
    {label:'Identify signs',   expression:'−84 ÷ (−7)'},
    {label:'Check sign rule',  expression:'Both negative → same signs'},
    {label:'Same signs',       expression:'Result will be POSITIVE'},
    {label:'Divide magnitudes',expression:'84 ÷ 7 = 12'},
    {label:'Answer',           expression:'= +12  ✓  Verify: −7 × 12 = −84', color:'#2E7D4F'},
  ],
};
