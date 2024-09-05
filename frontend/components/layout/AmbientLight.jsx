import React from 'react';

const AmbientLight = () => {
    return (
        <div className="absolute h-screen w-full overflow-hidden">
            <div className="absolute inset-0 flex justify-center items-start">
                <div className="relative w-[560px] h-[560px] -translate-y-[350px] -rotate-45">
                    <div className="absolute inset-0 bg-[radial-gradient(68.54%_68.72%_at_55.02%_31.46%,hsla(0,0%,85%,.08)_0,hsla(0,0%,55%,.02)_50%,hsla(0,0%,45%,0)_80%)]"></div>
                    <div className="absolute inset-0 bg-[radial-gradient(68.54%_68.72%_at_55.02%_31.46%,hsla(0,0%,85%,.08)_0,hsla(0,0%,55%,.02)_50%,hsla(0,0%,45%,0)_80%)] rotate-[150deg]"></div>
                    <div className="absolute inset-0 bg-[radial-gradient(68.54%_68.72%_at_55.02%_31.46%,hsla(0,0%,85%,.08)_0,hsla(0,0%,55%,.02)_50%,hsla(0,0%,45%,0)_80%)] rotate-[300deg]"></div>
                </div>
            </div>
        </div>
    );
};

export default AmbientLight;