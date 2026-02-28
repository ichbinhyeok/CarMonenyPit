@param String verdict
@param String brand
@param String status = ""

@template.layout(
    title = "Next Steps for " + brand, 
    extraStyles = @`
        <style>
            .lead-layout {
                display: flex;
                flex-direction: column;
                justify-content: center;
                align-items: center;
                min-height: 60vh;
                padding: 4rem 1.5rem;
            }
            .lead-card {
                background: var(--text-main); /* Dark slate */
                border-radius: 20px;
                box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
                border: 1px solid rgba(255, 255, 255, 0.1);
                color: #f1f5f9;
                max-width: 48rem;
                width: 100%;
                padding: 3.5rem 2.5rem;
                text-align: center;
            }
            .status-icon-wrapper {
                display: inline-flex;
                align-items: center;
                justify-content: center;
                width: 4.5rem;
                height: 4.5rem;
                border-radius: 50%;
                margin-bottom: 2rem;
            }
            .icon-red { background: rgba(244, 63, 94, 0.15); color: #fb7185; }
            .icon-green { background: rgba(16, 185, 129, 0.15); color: #34d399; }
            .icon-yellow { background: rgba(245, 158, 11, 0.15); color: #fbbf24; }
            
            .status-icon-wrapper svg { width: 2.5rem; height: 2.5rem; }
            
            .lead-heading {
                font-size: clamp(2rem, 5vw, 2.5rem);
                font-weight: 800;
                margin-bottom: 1.25rem;
                color: #fff;
                letter-spacing: -0.02em;
            }
            .lead-copy {
                color: #94a3b8;
                font-size: 1.125rem;
                line-height: 1.7;
                margin-bottom: 2.5rem;
                max-width: 36rem;
                margin-left: auto;
                margin-right: auto;
            }
            .form-box {
                max-width: 32rem;
                margin: 0 auto;
            }
            .form-row {
                display: flex;
                gap: 0.75rem;
            }
            .form-input-lead {
                flex: 1;
                background: rgba(15, 23, 42, 0.6);
                border: 1px solid rgba(255, 255, 255, 0.1);
                border-radius: 12px;
                padding: 1rem 1.25rem;
                color: #fff;
                font-size: 1rem;
                font-family: inherit;
                outline: none;
                transition: all 0.2s;
            }
            .form-input-lead:focus {
                border-color: #f59e0b;
                box-shadow: 0 0 0 3px rgba(245, 158, 11, 0.2);
            }
            .form-input-lead::placeholder { color: #64748b; }
            
            .btn-lead {
                background: #f59e0b;
                color: #000;
                border: none;
                border-radius: 12px;
                padding: 1rem 1.5rem;
                font-weight: 700;
                font-size: 1rem;
                cursor: pointer;
                transition: transform 0.2s, background 0.2s;
                white-space: nowrap;
            }
            .btn-lead:hover {
                background: #fbbf24;
                transform: translateY(-2px);
            }
            .disclaimer-txt {
                margin-top: 1.25rem;
                font-size: 0.8rem;
                color: #64748b;
            }
            
            .status-msg {
                padding: 1rem;
                border-radius: 12px;
                margin-bottom: 2rem;
                font-weight: 600;
                font-size: 0.95rem;
            }
            .msg-success { background: rgba(16, 185, 129, 0.1); border: 1px solid rgba(16, 185, 129, 0.2); color: #6ee7b7; }
            .msg-error { background: rgba(244, 63, 94, 0.1); border: 1px solid rgba(244, 63, 94, 0.2); color: #fda4af; }
            
            @media (max-width: 600px) {
                .form-row { flex-direction: column; }
                .btn-lead { width: 100%; }
            }
        </style>
    `,
    content = @`
        <div class="lead-layout">
            <div class="lead-card">
                @if("TIME_BOMB".equals(verdict))
                    <div class="status-icon-wrapper icon-red">
                        <svg fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
                        </svg>
                    </div>
                    <h2 class="lead-heading">Time to Sell Your ${brand}</h2>
                    <p class="lead-copy">We are evaluating partnerships with national wholesale buyers for vehicles like yours. Join the priority waitlist to get notified when our partner portal goes live to help you compare multiple offers.</p>
                @elseif("STABLE".equals(verdict))
                    <div class="status-icon-wrapper icon-green">
                        <svg fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                        </svg>
                    </div>
                    <h2 class="lead-heading">Protect Your ${brand}</h2>
                    <p class="lead-copy">Fixing it is the right call. We're finalizing partnerships with local mechanics and warranty providers to potentially offer you special assistance. Get notified when trusted options become available.</p>
                @else
                    <div class="status-icon-wrapper icon-yellow">
                        <svg fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                        </svg>
                    </div>
                    <h2 class="lead-heading">Next Steps for Your ${brand}</h2>
                    <p class="lead-copy">You're in a borderline situation where finding the best deal matters most. We are curating a list of vetted partners to help you decide. Drop your email below, and we'll send you our trusted recommendations once approved.</p>
                @endif

                @if("success".equals(status))
                    <div class="status-msg msg-success">
                        You're on the list. We will contact you when partner access is live.
                    </div>
                @elseif("invalid_email".equals(status))
                    <div class="status-msg msg-error">
                        Please enter a valid email address.
                    </div>
                @endif

                <form class="form-box" action="/waitlist/submit" method="POST" onsubmit="if(typeof gtag === 'function') gtag('event', 'submit_lead', { event_category: 'lead_capture', event_label: 'waitlist_form' });">
                    <input type="hidden" name="verdict" value="${verdict}">
                    <input type="hidden" name="brand" value="${brand}">
                    <input type="hidden" name="source" value="lead_capture">
                    <div class="form-row">
                        <input id="email" name="email" type="email" autocomplete="email" required class="form-input-lead" placeholder="Enter your email">
                        <button type="submit" class="btn-lead">
                            Join Priority List
                        </button>
                    </div>
                    <p class="disclaimer-txt">
                        * We never spam. You'll only hear from us when our trusted partner network is ready.
                    </p>
                </form>
            </div>
        </div>
    `
)
