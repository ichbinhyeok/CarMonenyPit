@param String verdict
@param String brand

@template.layout(
    title = "Next Steps for " + brand, 
    content = @`
        <div class="h-full flex flex-col justify-center max-w-2xl mx-auto py-12 px-4 sm:px-6 lg:px-8">
            <div class="bg-slate-800 rounded-lg shadow-xl outline outline-1 outline-slate-700/50 p-8 text-center text-slate-200">
                @if("TIME_BOMB".equals(verdict))
                    <div class="inline-flex items-center justify-center w-12 h-12 rounded-full bg-rose-500/20 mb-4">
                        <svg class="w-6 h-6 text-rose-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
                        </svg>
                    </div>
                    <h2 class="text-3xl font-bold mb-4">Time to Sell Your ${brand}</h2>
                    <p class="text-slate-400 mb-8 leading-relaxed">We are evaluating partnerships with national wholesale buyers for vehicles like yours. Join the priority waitlist to get notified when our partner portal goes live to help you compare multiple offers.</p>
                @elseif("STABLE".equals(verdict))
                    <div class="inline-flex items-center justify-center w-12 h-12 rounded-full bg-emerald-500/20 mb-4">
                        <svg class="w-6 h-6 text-emerald-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                        </svg>
                    </div>
                    <h2 class="text-3xl font-bold mb-4">Protect Your ${brand}</h2>
                    <p class="text-slate-400 mb-8 leading-relaxed">Fixing it is the right call. We're finalizing partnerships with local mechanics and warranty providers to potentially offer you special assistance. Get notified when trusted options become available.</p>
                @else
                    <div class="inline-flex items-center justify-center w-12 h-12 rounded-full bg-amber-500/20 mb-4">
                        <svg class="w-6 h-6 text-amber-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                        </svg>
                    </div>
                    <h2 class="text-3xl font-bold mb-4">Next Steps for Your ${brand}</h2>
                    <p class="text-slate-400 mb-8 leading-relaxed">You're in a borderline situation where finding the best deal matters most. We are curating a list of vetted partners to help you decide. Drop your email below, and we'll send you our trusted recommendations once approved.</p>
                @endif

                <form class="max-w-md mx-auto" action="/" method="GET" onsubmit="event.preventDefault(); alert('Thanks! You have been added to the priority notification list.'); window.location.href='/';">
                    <div class="flex gap-2">
                        <label for="email" class="sr-only">Email address</label>
                        <input id="email" name="email" type="email" autocomplete="email" required class="min-w-0 flex-auto rounded-md border-0 bg-slate-900/50 px-3.5 py-2 text-white shadow-sm ring-1 ring-inset ring-slate-700/50 focus:ring-2 focus:ring-inset focus:ring-amber-500 sm:text-sm sm:leading-6 placeholder-slate-500 transition-colors" placeholder="Enter your email">
                        <button type="submit" class="flex-none rounded-md bg-amber-500 px-3.5 py-2.5 text-sm font-semibold text-slate-900 shadow-sm hover:bg-amber-400 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-amber-500 transition-colors">
                            Notify Me instantly
                        </button>
                    </div>
                    <p class="mt-4 text-xs text-slate-500">
                        * We never spam. You'll only hear from us when our trusted partner network is ready.
                    </p>
                </form>
            </div>
        </div>
    `
)
