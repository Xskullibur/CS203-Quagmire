// components/main/PreFooter.jsx
export default function PreFooter() {
    return (
        <section className="pre-footer w-full py-24 px-24 bg-[linear-gradient(0deg,#141516 55.66%,var(--color-bg-primary) 100%)]">
            <div className="container mx-auto px-4">
                <div className="flex justify-between items-center">
                    <h2 className="text-white text-5xl font-bold leading-tight">
                        Interested in hosting<br />
                        your own tournaments?
                    </h2>
                    <div className="space-x-4">
                        <button className="bg-white text-black px-4 py-2 rounded-full">
                            Get started
                        </button>

                    </div>
                </div>
            </div>
        </section>
    );
}